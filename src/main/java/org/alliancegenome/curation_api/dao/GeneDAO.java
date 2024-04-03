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
	@Inject GenePhenotypeAnnotationDAO genePhenotypeAnnotationDAO;

	protected GeneDAO() {
		super(Gene.class);
	}

	public List<Long> findReferencingDiseaseAnnotations(Long geneId) {
		Map<String, Object> params = new HashMap<>();
		params.put("diseaseAnnotationSubject.id", geneId);
		List<Long> results = geneDiseaseAnnotationDAO.findFilteredIds(params);
		
		Map<String, Object> dgmParams = new HashMap<>();
		dgmParams.put("diseaseGeneticModifiers.id", geneId);
		results.addAll(diseaseAnnotationDAO.findFilteredIds(dgmParams));
		
		Map<String, Object> agmAgParams = new HashMap<>();
		agmAgParams.put("assertedGenes.id", geneId);
		results.addAll(agmDiseaseAnnotationDAO.findFilteredIds(agmAgParams));
		
		Map<String, Object> agmIgParams = new HashMap<>();
		agmIgParams.put("inferredGene.id", geneId);
		results.addAll(agmDiseaseAnnotationDAO.findFilteredIds(agmIgParams));
		
		Map<String, Object> alleleAgParams = new HashMap<>();
		alleleAgParams.put("assertedGenes.id", geneId);
		results.addAll(alleleDiseaseAnnotationDAO.findFilteredIds(alleleAgParams));
		
		Map<String, Object> alleleIgParams = new HashMap<>();
		alleleIgParams.put("inferredGene.id", geneId);
		results.addAll(alleleDiseaseAnnotationDAO.findFilteredIds(alleleIgParams));
		
		Map<String, Object> withParams = new HashMap<>();
		withParams.put("with.id", geneId);
		results.addAll(diseaseAnnotationDAO.findFilteredIds(withParams));
		
		return results;
	}
	
	public List<Long> findReferencingInteractions(Long geneId) {
		Map<String, Object> subjectParams = new HashMap<>();
		subjectParams.put("geneAssociationSubject.id", geneId);
		List<Long> results = geneInteractionDAO.findFilteredIds(subjectParams);
		
		Map<String, Object> objectParams = new HashMap<>();
		objectParams.put("geneGeneAssociationObject.id", geneId);
		results.addAll(geneInteractionDAO.findFilteredIds(objectParams));
		
		return results;
	}

	public List<Long> findReferencingOrthologyPairs(Long geneId) {
		Map<String, Object> subjectParams = new HashMap<>();
		subjectParams.put("subjectGene.id", geneId);
		List<Long> results = geneToGeneOrthologyDAO.findFilteredIds(subjectParams);
		
		Map<String, Object> objectParams = new HashMap<>();
		objectParams.put("objectGene.id", geneId);
		results.addAll(geneToGeneOrthologyDAO.findFilteredIds(objectParams));
		
		return results;
	}

	public List<Long> findReferencingPhenotypeAnnotations(Long geneId) {
		Map<String, Object> params = new HashMap<>();
		params.put("phenotypeAnnotationSubject.id", geneId);
		List<Long> results = genePhenotypeAnnotationDAO.findFilteredIds(params);

		return results;
	}
}
