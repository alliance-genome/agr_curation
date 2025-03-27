package org.alliancegenome.curation_api.model.document.builders;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.alliancegenome.curation_api.model.document.es.DiseaseSearchResultDocument;
import org.alliancegenome.curation_api.model.document.es.DiseaseSummaryDocument;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.AGMDiseaseAnnotationService;
import org.alliancegenome.curation_api.services.AlleleDiseaseAnnotationService;
import org.alliancegenome.curation_api.services.GeneDiseaseAnnotationService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequestScoped
public class DiseaseSummaryDocumentBuilder {

	@Inject
	GeneDiseaseAnnotationService service;
	@Inject
	AGMDiseaseAnnotationService agmService;
	@Inject
	AlleleDiseaseAnnotationService alleleService;

	public DiseaseSummaryDocument buildSummaryDocument(DOTerm doTerm) {

		DiseaseSummaryDocument doc = new DiseaseSummaryDocument();

		doc.setDoTerm(doTerm);
		doc.setParents(doTerm.getIsaParents());
		doc.setChildren(doTerm.getIsaChildren());
		doc.setCrossReferenceLinkUrls(new ArrayList<Map<String, String>>());
		for (CrossReference cr : doTerm.getCrossReferences()) {
			Map<String, String> map = new HashMap<>();
			map.put("referencedCurie", cr.getReferencedCurie());
			map.put("url", cr.getUrlFromResourceDescriptorPage(doTerm.getCurie()));
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
		doc.setSymbol(doTerm.getName());
		doc.setName(doTerm.getName());
		doc.setNameKey(doTerm.getName());
		doc.setSynonyms(doTerm.getSynonyms().stream().map(Synonym::getName).collect(Collectors.toSet()));
		doc.setCrossReferences(doTerm.getCrossReferences().stream().map(CrossReference::getDisplayName).collect(Collectors.toSet()));
		doc.setSecondaryIds(new HashSet<>(doTerm.getSecondaryIdentifiers()));

		// add genes from GeneDiseaseAnnotations
		List<GeneDiseaseAnnotation> geneDiseaseAnnotations = doTerm.getGeneDiseaseAnnotations();
		doc.setGenes(geneDiseaseAnnotations.stream().map(geneDiseaseAnnotation -> geneDiseaseAnnotation.getDiseaseAnnotationSubject().getGeneSymbol().getDisplayText()).collect(Collectors.toSet()));
		doc.setAssociatedSpecies(geneDiseaseAnnotations.stream().map(geneDiseaseAnnotation -> geneDiseaseAnnotation.getDiseaseAnnotationSubject().getTaxon().getGenusSpecies()).collect(Collectors.toSet()));

		// collect all the involved genes: direct genes, inferred genes, and asserted genes
		// Needed to retrieve the orthologous genes
		Set<Gene> allInvolvedGenes = geneDiseaseAnnotations.stream().map(GeneDiseaseAnnotation::getDiseaseAnnotationSubject).collect(Collectors.toSet());

		// loop over AGMDiseaseAnnotation
		List<AGMDiseaseAnnotation> agmDiseaseAnnotations = doTerm.getAGMDiseaseAnnotations();
		if (agmDiseaseAnnotations != null) {
			Set<Gene> inferredGene = getSingleGenes(agmDiseaseAnnotations, AGMDiseaseAnnotation::getInferredGene);
			allInvolvedGenes.addAll(inferredGene);
			doc.getGenes().addAll(inferredGene.stream().map(this::getGeneName).collect(Collectors.toSet()));
			Collection<Gene> assertedGenes = getMultipleGenes(agmDiseaseAnnotations, AGMDiseaseAnnotation::getAssertedGenes);
			doc.getGenes().addAll(assertedGenes.stream().map(this::getGeneName).collect(Collectors.toSet()));
			allInvolvedGenes.addAll(assertedGenes);
			doc.getAlleles().addAll(
				agmDiseaseAnnotations.stream()
					.filter(agmDiseaseAnnotation -> agmDiseaseAnnotation.getInferredAllele() != null)
					.map(alleleDiseaseAnnotation -> getAlleleName(alleleDiseaseAnnotation.getInferredAllele()))
					.collect(Collectors.toSet()));
			doc.setModels(agmDiseaseAnnotations.stream().map(agmDiseaseAnnotation -> agmDiseaseAnnotation.getDiseaseAnnotationSubject().getName()).collect(Collectors.toSet()));
		}
		// loop over AlleleDiseaseAnnotations
		List<AlleleDiseaseAnnotation> alleleDiseaseAnnotations = doTerm.getAlleleDiseaseAnnotations();
		if (alleleDiseaseAnnotations != null) {
			Set<Gene> inferredGenes = getSingleGenes(alleleDiseaseAnnotations, AlleleDiseaseAnnotation::getInferredGene);
			allInvolvedGenes.addAll(inferredGenes);
			doc.getGenes().addAll(inferredGenes.stream().map(this::getGeneName).collect(Collectors.toSet()));

			Set<Gene> assertedGenes = getMultipleAlleles(alleleDiseaseAnnotations, AlleleDiseaseAnnotation::getAssertedGenes);
			allInvolvedGenes.addAll(assertedGenes);
			doc.getGenes().addAll(assertedGenes.stream().map(this::getGeneName).collect(Collectors.toSet()));
			doc.setAlleles(alleleDiseaseAnnotations.stream()
				.filter(alleleDiseaseAnnotation -> alleleDiseaseAnnotation.getDiseaseAnnotationSubject().getAlleleSymbol() != null)
				.map(alleleDiseaseAnnotation -> getAlleleName(alleleDiseaseAnnotation.getDiseaseAnnotationSubject()))
				.collect(Collectors.toSet()));
		}

		// add orthologous genes for the all-involved genes
		allInvolvedGenes.forEach(gene -> gene.getGeneToGeneOrthologyGenerateds().forEach(orthology -> {
			doc.getGenes().add(getGeneName(orthology.getObjectGene()));
			doc.getAssociatedSpecies().add(orthology.getObjectGene().getTaxon().getGenusSpecies());
		}));
		doc.setParentDiseaseNames(doTerm.getIsaAncestors().stream().map(OntologyTerm::getName).collect(Collectors.toSet()));

		// calculate diseaseGroup, ie parents with subset DO_AGR_slim
		doc.setDiseaseGroup(doTerm.getIsaAncestors().stream()
			.filter(ontologyTerm -> ontologyTerm.getSubsets().contains("DO_AGR_slim"))
			.map(OntologyTerm::getName)
			.collect(Collectors.toSet()));
		return doc;
	}

	private String getAlleleName(Allele allele) {
		return allele.getAlleleSymbol().getDisplayText() + getSpeciesAbbrev(allele);
	}

	private String getGeneName(Gene gene) {
		return gene.getGeneSymbol().getDisplayText() + getSpeciesAbbrev(gene);
	}

	private String getSpeciesAbbrev(GenomicEntity genomicEntity) {
		return " (" + genomicEntity.getTaxon().getSpecies().get(0).getAbbreviation() + ")";
	}

	public Set<Gene> getSingleGenes(Collection<AGMDiseaseAnnotation> annotations, Function<AGMDiseaseAnnotation, Gene> function) {
		return annotations.stream()
			.filter(agmDiseaseAnnotation -> function.apply(agmDiseaseAnnotation) != null)
			.map(function)
			.collect(Collectors.toSet());
	}

	public Set<Gene> getMultipleGenes(List<AGMDiseaseAnnotation> annotations, Function<AGMDiseaseAnnotation, List<Gene>> function) {
		return annotations.stream()
			.filter(agmDiseaseAnnotation -> function.apply(agmDiseaseAnnotation) != null)
			.map(geneDiseaseAnnotation -> function.apply(geneDiseaseAnnotation).stream()
				.toList())
			.flatMap(Collection::stream)
			.collect(Collectors.toSet());
	}

	public Set<Gene> getSingleGenes(List<AlleleDiseaseAnnotation> annotations, Function<AlleleDiseaseAnnotation, Gene> function) {
		return annotations.stream()
			.filter(agmDiseaseAnnotation -> function.apply(agmDiseaseAnnotation) != null)
			.map(function)
			.collect(Collectors.toSet());
	}

	public Set<Gene> getMultipleAlleles(List<AlleleDiseaseAnnotation> annotations, Function<AlleleDiseaseAnnotation, List<Gene>> function) {
		return annotations.stream()
			.filter(agmDiseaseAnnotation -> function.apply(agmDiseaseAnnotation) != null)
			.map(geneDiseaseAnnotation -> function.apply(geneDiseaseAnnotation).stream()
				.toList())
			.flatMap(Collection::stream)
			.collect(Collectors.toSet());
	}
}
