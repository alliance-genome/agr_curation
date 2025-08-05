package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.document.es.TransgenicAlleleDocument;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransgenicAlleleDocumentBuilder {

	public List<TransgenicAlleleDocument> buildTransgenicAlleleDocument(AlleleConstructAssociation association) {
		if (CollectionUtils.isEmpty(association.getAlleleConstructAssociationObject().getConstructGenomicEntityAssociations())) {
			return null;
		}
		Allele allele = association.getAlleleAssociationSubject();
		// check AlleleDiseaseAnnotations and AGMDiseaseAnnotations with inferred or asserted alleles for disease annotations
		List<AGMDiseaseAnnotation> agmDiseaseAnnotations = allele.getAgmDiseaseAssertedAlleleAnnotations();
		agmDiseaseAnnotations.addAll(allele.getAgmDiseaseInferredAlleleAnnotations());
		Boolean hasDiseaseAnnotation = CollectionUtils.isNotEmpty(agmDiseaseAnnotations) || CollectionUtils.isNotEmpty(allele.getAlleleDiseaseAnnotations());

		// check AllelePhenotypeAnnotations and AGMPhenotypeAnnotations with inferred or asserted alleles for phenotype annotations
		List<AGMPhenotypeAnnotation> agmPhenotypeAnnotations = allele.getAgmPhenotypeAssertedAlleleAnnotations();
		agmPhenotypeAnnotations.addAll(allele.getAgmPhenotypeInferredAlleleAnnotations());
		Boolean hasPhenotypeAnnotation = CollectionUtils.isNotEmpty(agmPhenotypeAnnotations) || CollectionUtils.isNotEmpty(allele.getAllelePhenotypeAnnotations());

		List<TransgenicAlleleDocument> transgenicAlleleDocuments = new ArrayList<>();
		// get all genes that have a construct-expresses-gene relationship
		Map<Gene, List<Construct>> expressedGeneConstructMap = getExpressedGeneConstructMap(association);
		expressedGeneConstructMap.forEach(
				(gene, constructs) -> {
					TransgenicAlleleDocument transAllele = new TransgenicAlleleDocument();
					transAllele.setAllele(allele);
					transAllele.setGene(gene);
					transAllele.setConstructs(constructs);
					List<Gene> genes = new ArrayList<>(constructs.stream().flatMap(construct -> getExpressedGeneList(construct, "expresses").stream()).toList());
					List<Gene> nonBgiComponents = getNonBgiComponents(constructs);
					genes.addAll(nonBgiComponents);
					transAllele.setExpressedGenes(genes);
					List<Gene> regulatorGenes = constructs.stream().flatMap(construct -> getExpressedGeneList(construct, "is_regulated_by").stream()).toList();
					transAllele.setRegulatoryGenes(regulatorGenes);
					transAllele.setHasDiseaseAnnotations(hasDiseaseAnnotation);
					transAllele.setHasPhenotypeAnnotations(hasPhenotypeAnnotation);
					transgenicAlleleDocuments.add(transAllele);
				});
		return transgenicAlleleDocuments;
	}

	@NotNull
	private static List<Gene> getNonBgiComponents(List<Construct> constructs) {
		List<ConstructComponentSlotAnnotation> annotations = constructs
				.stream()
				.flatMap(construct -> construct.getConstructComponents().stream())
				.toList();
		return annotations.stream()
				.map(annotation -> {
					Gene nonBgiGene = new Gene();
					GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
					symbol.setDisplayText(annotation.getComponentSymbol());
					symbol.setFormatText(annotation.getComponentSymbol());
					nonBgiGene.setGeneSymbol(symbol);
					return nonBgiGene;
				})
				.toList();
	}

	private Map<Gene, List<Construct>> getExpressedGeneConstructMap(AlleleConstructAssociation association) {
		Map<Gene, List<Construct>> geneConstructMap = new HashMap<>();
		association
				.getAlleleConstructAssociationObject()
				.getConstructGenomicEntityAssociations()
				.forEach(constructGenomicEntityAssociation -> {
							if (constructGenomicEntityAssociation.getConstructGenomicEntityAssociationObject() instanceof Gene Gene
									&& constructGenomicEntityAssociation.getRelation().equals(getConstructRelation("expresses"))) {
								List<Construct> constructList = geneConstructMap.computeIfAbsent(Gene, k -> new ArrayList<>());
								constructList.add(association.getAlleleConstructAssociationObject());
							}
						}
				);

		return geneConstructMap;
	}

	private List<Gene> getExpressedGeneList(Construct construct, String relationName) {
		List<Gene> expressedGenes = new ArrayList<>();
		construct.getConstructGenomicEntityAssociations()
				.forEach(constructGenomicEntityAssociation -> {
							if (constructGenomicEntityAssociation.getConstructGenomicEntityAssociationObject() instanceof Gene gene
									&& constructGenomicEntityAssociation.getRelation().equals(getConstructRelation(relationName))) {
								expressedGenes.add(gene);
							}
						}
				);
		return expressedGenes;
	}

	private HashMap<String, VocabularyTerm> terms;
	VocabularyTermDAO vocabularyTermDAO;

	public void setVocabularyTermDAO(VocabularyTermDAO vocabularyTermDAO) {
		this.vocabularyTermDAO = vocabularyTermDAO;
	}

	private VocabularyTerm getConstructRelation(String termName) {
		if (terms != null) {
			return terms.get(termName);
		}

		HashMap<String, Object> params = new HashMap<>();
		params.put("vocabulary.name", "Construct Relation");
		SearchResponse<VocabularyTerm> response = vocabularyTermDAO.findByParams(new Pagination(), params);
		terms = new HashMap<>();
		for (VocabularyTerm vt : response.getResults()) {
			terms.put(vt.getName(), vt);
		}
		return terms.get(termName);
	}
}
