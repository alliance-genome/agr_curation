package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyDAO;
import org.alliancegenome.curation_api.model.entities.Gene;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GeneDAO extends BaseSQLDAO<Gene> {

	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject GeneToGeneOrthologyDAO geneToGeneOrthologyDAO;
	@Inject GeneInteractionDAO geneInteractionDAO;
	@Inject AllelePhenotypeAnnotationDAO allelePhenotypeAnnotationDAO;
	@Inject AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject GenePhenotypeAnnotationDAO genePhenotypeAnnotationDAO;
	@Inject GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;

	protected GeneDAO() {
		super(Gene.class);
	}

	public List<Long> findReferencingDiseaseAnnotations(Long geneId) {
		Map<String, Object> params = new HashMap<>();
		params.put("diseaseAnnotationSubject.id", geneId);
		List<Long> results = geneDiseaseAnnotationDAO.findIdsByParams(params);

		Map<String, Object> dgmParams = new HashMap<>();
		dgmParams.put("diseaseGeneticModifiers.id", geneId);
		results.addAll(diseaseAnnotationDAO.findIdsByParams(dgmParams));

		Map<String, Object> agmAgParams = new HashMap<>();
		agmAgParams.put("assertedGenes.id", geneId);
		results.addAll(agmDiseaseAnnotationDAO.findIdsByParams(agmAgParams));

		Map<String, Object> agmIgParams = new HashMap<>();
		agmIgParams.put("inferredGene.id", geneId);
		results.addAll(agmDiseaseAnnotationDAO.findIdsByParams(agmIgParams));

		Map<String, Object> alleleAgParams = new HashMap<>();
		alleleAgParams.put("assertedGenes.id", geneId);
		results.addAll(alleleDiseaseAnnotationDAO.findIdsByParams(alleleAgParams));

		Map<String, Object> alleleIgParams = new HashMap<>();
		alleleIgParams.put("inferredGene.id", geneId);
		results.addAll(alleleDiseaseAnnotationDAO.findIdsByParams(alleleIgParams));

		Map<String, Object> withParams = new HashMap<>();
		withParams.put("with.id", geneId);
		results.addAll(diseaseAnnotationDAO.findIdsByParams(withParams));

		return results;
	}

	public List<Long> findReferencingInteractions(Long geneId) {
		Map<String, Object> subjectParams = new HashMap<>();
		subjectParams.put("geneAssociationSubject.id", geneId);
		List<Long> results = geneInteractionDAO.findIdsByParams(subjectParams);

		Map<String, Object> objectParams = new HashMap<>();
		objectParams.put("geneGeneAssociationObject.id", geneId);
		results.addAll(geneInteractionDAO.findIdsByParams(objectParams));

		return results;
	}

	public List<Long> findReferencingOrthologyPairs(Long geneId) {
		Map<String, Object> subjectParams = new HashMap<>();
		subjectParams.put("subjectGene.id", geneId);
		List<Long> results = geneToGeneOrthologyDAO.findIdsByParams(subjectParams);

		Map<String, Object> objectParams = new HashMap<>();
		objectParams.put("objectGene.id", geneId);
		results.addAll(geneToGeneOrthologyDAO.findIdsByParams(objectParams));

		return results;
	}

	public List<Long> findReferencingPhenotypeAnnotations(Long geneId) {
		Map<String, Object> params = new HashMap<>();
		params.put("phenotypeAnnotationSubject.id", geneId);

		List<Long> results = genePhenotypeAnnotationDAO.findIdsByParams(params);

		Map<String, Object> agmAgParams = new HashMap<>();
		agmAgParams.put("assertedGenes.id", geneId);
		results.addAll(agmPhenotypeAnnotationDAO.findIdsByParams(agmAgParams));

		Map<String, Object> agmIgParams = new HashMap<>();
		agmIgParams.put("inferredGene.id", geneId);
		results.addAll(agmPhenotypeAnnotationDAO.findIdsByParams(agmIgParams));

		Map<String, Object> alleleAgParams = new HashMap<>();
		alleleAgParams.put("assertedGenes.id", geneId);
		results.addAll(allelePhenotypeAnnotationDAO.findIdsByParams(alleleAgParams));

		Map<String, Object> alleleIgParams = new HashMap<>();
		alleleIgParams.put("inferredGene.id", geneId);
		results.addAll(allelePhenotypeAnnotationDAO.findIdsByParams(alleleIgParams));

		return results;
	}

	public List<Long> findReferencingGeneExpressionAnnotations(Long geneId) {
		Map<String, Object> params = new HashMap<>();
		params.put("expressionAnnotationSubject.id", geneId);
		return geneExpressionAnnotationDAO.findIdsByParams(params);
	}
}
