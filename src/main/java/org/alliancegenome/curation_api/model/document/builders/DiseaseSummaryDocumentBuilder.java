package org.alliancegenome.curation_api.model.document.builders;

import lombok.extern.slf4j.Slf4j;
import org.alliancegenome.curation_api.model.document.es.DiseaseSearchResultDocument;
import org.alliancegenome.curation_api.model.document.es.DiseaseSummaryDocument;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class DiseaseSummaryDocumentBuilder {

	public DiseaseSummaryDocument buildSummaryDocument(DOTerm doTerm) {

		DiseaseSummaryDocument doc = new DiseaseSummaryDocument();

		doc.setDoTerm(doTerm);
		doc.setParents(doTerm.getIsaParents());
		doc.setChildren(doTerm.getIsaChildren());
		doc.setCrossReferenceLinkUrls(new ArrayList<>());
		for (CrossReference cr : doTerm.getCrossReferences()) {
			Map<String, String> map = new HashMap<>();
			map.put("referencedCurie", cr.getReferencedCurie());
			map.put("url", cr.getUrlFromResourceDescriptorPage(cr.getReferencedCurie()));
			doc.getCrossReferenceLinkUrls().add(map);
		}

		return doc;
	}

	public DiseaseSearchResultDocument buildSearchResultDocument(DOTerm doTerm) {
		DiseaseSearchResultDocument doc = new DiseaseSearchResultDocument();

		doc.setCurie(doTerm.getCurie());
		doc.setPrimaryKey(doTerm.getCurie());
		doc.setSearchable(false);
		doc.setDefinition(doTerm.getDefinition());
		doc.setName(doTerm.getName());
		doc.setNameKey(doTerm.getName());
		doc.setSynonyms(doTerm.getSynonyms().stream().map(Synonym::getName).collect(Collectors.toSet()));
		doc.setCrossReferences(doTerm.getCrossReferences().stream().map(CrossReference::getDisplayName).collect(Collectors.toSet()));
		doc.setSecondaryIds(new HashSet<>(doTerm.getSecondaryIdentifiers()));

		Set<Gene> allInvolvedGenes = new HashSet<>();
		// add genes from GeneDiseaseAnnotations
		List<GeneDiseaseAnnotation> geneDiseaseAnnotations = doTerm.getPublicGeneDiseaseAnnotations();
		if (CollectionUtils.isNotEmpty(geneDiseaseAnnotations)) {
			doc.setGenes(geneDiseaseAnnotations.stream().map(geneDiseaseAnnotation -> getGeneName(geneDiseaseAnnotation.getDiseaseAnnotationSubject())).collect(Collectors.toSet()));
			doc.setAssociatedSpecies(geneDiseaseAnnotations.stream().map(geneDiseaseAnnotation -> geneDiseaseAnnotation.getDiseaseAnnotationSubject().getTaxon().getGenusSpecies()).collect(Collectors.toSet()));

			// collect all the involved genes: direct genes, inferred genes, and asserted
			// genes
			// Needed to retrieve the orthologous genes
			allInvolvedGenes = geneDiseaseAnnotations.stream().map(GeneDiseaseAnnotation::getDiseaseAnnotationSubject).collect(Collectors.toSet());
		}


		// loop over AGMDiseaseAnnotation
		List<AGMDiseaseAnnotation> agmDiseaseAnnotations = doTerm.getPublicAGMDiseaseAnnotations();
		if (agmDiseaseAnnotations != null) {
			Set<Gene> inferredGene = getSingleGenes(agmDiseaseAnnotations, AGMDiseaseAnnotation::getInferredGene);
			allInvolvedGenes.addAll(inferredGene);
			doc.getGenes().addAll(inferredGene.stream().map(this::getGeneName).collect(Collectors.toSet()));
			Collection<Gene> assertedGenes = getMultipleGenes(agmDiseaseAnnotations, AGMDiseaseAnnotation::getAssertedGenes);
			doc.getGenes().addAll(assertedGenes.stream().map(this::getGeneName).collect(Collectors.toSet()));
			allInvolvedGenes.addAll(assertedGenes);
			doc.getAlleles().addAll(agmDiseaseAnnotations.stream().filter(agmDiseaseAnnotation -> agmDiseaseAnnotation.getInferredAllele() != null).map(alleleDiseaseAnnotation -> getAlleleName(alleleDiseaseAnnotation.getInferredAllele())).collect(Collectors.toSet()));
			doc.setModels(agmDiseaseAnnotations.stream().map(agmDiseaseAnnotation -> getModelName(agmDiseaseAnnotation.getDiseaseAnnotationSubject())).collect(Collectors.toSet()));
		}
		// loop over AlleleDiseaseAnnotations
		List<AlleleDiseaseAnnotation> alleleDiseaseAnnotations = doTerm.getPublicAlleleDiseaseAnnotations();
		if (alleleDiseaseAnnotations != null) {
			Set<Gene> inferredGenes = getSingleGenes(alleleDiseaseAnnotations, AlleleDiseaseAnnotation::getInferredGene);
			allInvolvedGenes.addAll(inferredGenes);
			doc.getGenes().addAll(inferredGenes.stream().map(this::getGeneName).collect(Collectors.toSet()));

			Set<Gene> assertedGenes = getMultipleAlleles(alleleDiseaseAnnotations, AlleleDiseaseAnnotation::getAssertedGenes);
			allInvolvedGenes.addAll(assertedGenes);
			doc.getGenes().addAll(assertedGenes.stream().map(this::getGeneName).collect(Collectors.toSet()));
			doc.getAlleles().addAll(alleleDiseaseAnnotations.stream()
				.filter(alleleDiseaseAnnotation -> alleleDiseaseAnnotation.getDiseaseAnnotationSubject().getAlleleSymbol() != null)
				.map(alleleDiseaseAnnotation -> getAlleleName(alleleDiseaseAnnotation.getDiseaseAnnotationSubject())).collect(Collectors.toSet())
			);
		}

		// add orthologous genes for the all-involved genes (only
		allInvolvedGenes.forEach(gene -> gene.getGeneToGeneOrthologyGenerateds()
			.stream().filter(GeneToGeneOrthologyGenerated::getStrictFilter).forEach(orthology -> {
				doc.getGenes().add(getGeneName(orthology.getObjectGene()));
				doc.getAssociatedSpecies().add(orthology.getObjectGene().getTaxon().getGenusSpecies());
			}));
		doc.setParentDiseaseNames(doTerm.getIsaAncestors().stream().map(OntologyTerm::getName).collect(Collectors.toSet()));
		// add self to the list
		doc.getParentDiseaseNames().add(doTerm.getName());

		// calculate diseaseGroup, ie parents with subset DO_AGR_slim
		doc.setDiseaseGroup(doTerm.getIsaAncestors().stream().filter(ontologyTerm -> ontologyTerm.getSubsets().contains("DO_AGR_slim")).map(OntologyTerm::getName).collect(Collectors.toSet()));
		return doc;
	}

	private String getAlleleName(Allele allele) {
		return allele.getAlleleSymbol().getFormatText() + getSpeciesAbbrev(allele);
	}

	private String getModelName(AffectedGenomicModel model) {
		return model.getNameFormatText() + getSpeciesAbbrev(model);
	}

	private String getGeneName(Gene gene) {
		return gene.getGeneSymbol().getDisplayText() + getSpeciesAbbrev(gene);
	}

	private String getSpeciesAbbrev(GenomicEntity genomicEntity) {
		return " (" + genomicEntity.getTaxon().getSpecies().get(0).getAbbreviation() + ")";
	}

	public Set<Gene> getSingleGenes(Collection<AGMDiseaseAnnotation> annotations, Function<AGMDiseaseAnnotation, Gene> function) {
		return annotations.stream().filter(agmDiseaseAnnotation -> function.apply(agmDiseaseAnnotation) != null).map(function).collect(Collectors.toSet());
	}

	public Set<Gene> getMultipleGenes(List<AGMDiseaseAnnotation> annotations, Function<AGMDiseaseAnnotation, List<Gene>> function) {
		return annotations.stream().filter(agmDiseaseAnnotation -> function.apply(agmDiseaseAnnotation) != null).map(geneDiseaseAnnotation -> function.apply(geneDiseaseAnnotation).stream().toList()).flatMap(Collection::stream).collect(Collectors.toSet());
	}

	public Set<Gene> getSingleGenes(List<AlleleDiseaseAnnotation> annotations, Function<AlleleDiseaseAnnotation, Gene> function) {
		return annotations.stream().filter(agmDiseaseAnnotation -> function.apply(agmDiseaseAnnotation) != null).map(function).collect(Collectors.toSet());
	}

	public Set<Gene> getMultipleAlleles(List<AlleleDiseaseAnnotation> annotations, Function<AlleleDiseaseAnnotation, List<Gene>> function) {
		return annotations.stream().filter(agmDiseaseAnnotation -> function.apply(agmDiseaseAnnotation) != null).map(geneDiseaseAnnotation -> function.apply(geneDiseaseAnnotation).stream().toList()).flatMap(Collection::stream).collect(Collectors.toSet());
	}
}
