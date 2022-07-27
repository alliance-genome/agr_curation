package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.AGMDiseaseAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class AGMDiseaseAnnotationService extends BaseCrudService<AGMDiseaseAnnotation, AGMDiseaseAnnotationDAO> {

	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject AffectedGenomicModelDAO agmDAO;
	@Inject GeneDAO geneDAO;
	@Inject AlleleDAO alleleDAO;
	@Inject NoteDAO noteDAO;
	@Inject AGMDiseaseAnnotationValidator agmDiseaseValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject ConditionRelationDAO conditionRelationDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmDiseaseAnnotationDAO);
	}
	
	@Override
	@Transactional
	public ObjectResponse<AGMDiseaseAnnotation> update(AGMDiseaseAnnotation uiEntity) {
		AGMDiseaseAnnotation dbEntity = agmDiseaseValidator.validateAnnotation(uiEntity);
		if (CollectionUtils.isNotEmpty(dbEntity.getRelatedNotes())) {
			for (Note note : dbEntity.getRelatedNotes()) {
				noteDAO.persist(note);
			}
		}
		agmDiseaseAnnotationDAO.persist(dbEntity);
		
		// TODO this return needs to be changed back to the dbEntity in order for new items (notes) to be created properly 
		return new ObjectResponse<>(dbEntity);
	}

	@Transactional
	public AGMDiseaseAnnotation upsert(AGMDiseaseAnnotationDTO dto) throws ObjectUpdateException {
		AGMDiseaseAnnotation annotation = validateAGMDiseaseAnnotationDTO(dto);
		if (annotation == null) throw new ObjectUpdateException(dto, "Validation Failed");
		
		annotation = (AGMDiseaseAnnotation) diseaseAnnotationService.upsert(annotation, dto);
		if (annotation != null) {
			agmDiseaseAnnotationDAO.persist(annotation);
		}
		return annotation;
	}
	
	private AGMDiseaseAnnotation validateAGMDiseaseAnnotationDTO(AGMDiseaseAnnotationDTO dto) throws ObjectUpdateException, ObjectValidationException {
		AGMDiseaseAnnotation annotation;
		if (dto.getSubject() == null) {
			throw new ObjectValidationException(dto, "Annotation for " + dto.getObject() + " missing a subject AGM - skipping");
		}
		
		AffectedGenomicModel agm = agmDAO.find(dto.getSubject());
		if (agm == null) {
			throw new ObjectValidationException(dto, "AGM " + dto.getSubject() + " not found in database - skipping annotation");
		}
		
		String annotationId = dto.getModEntityId();
		if (annotationId == null) {
			annotationId = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(agm.getTaxon().getCurie()).getCurieID(dto);
		}
		SearchResponse<AGMDiseaseAnnotation> annotationList = agmDiseaseAnnotationDAO.findByField("uniqueId", annotationId);
		if (annotationList == null || annotationList.getResults().size() == 0) {
			annotation = new AGMDiseaseAnnotation();
			annotation.setUniqueId(annotationId);
			annotation.setSubject(agm);
		} else {
			annotation = annotationList.getResults().get(0);
		}
		
		annotation = (AGMDiseaseAnnotation) diseaseAnnotationService.validateAnnotationDTO(annotation, dto);
		if (annotation == null) return null;
		
		VocabularyTerm diseaseRelation = vocabularyTermDAO.getTermInVocabulary(dto.getDiseaseRelation(), VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY);
		if (diseaseRelation == null) {
			throw new ObjectValidationException(dto, "Invalid AGM disease relation for " + annotationId + " - skipping");
		}
		annotation.setDiseaseRelation(diseaseRelation);
		
		if (dto.getInferredGene() != null) {
			Gene inferredGene = geneDAO.find(dto.getInferredGene());
			if (inferredGene == null)
				throw new ObjectValidationException(dto, "Invalid inferred gene for " + annotationId + " - skipping");
			annotation.setInferredGene(inferredGene);
		}

		if (dto.getAssertedGene() != null) {
			Gene assertedGene = geneDAO.find(dto.getAssertedGene());
			if (assertedGene == null)
				throw new ObjectValidationException(dto, "Invalid asserted gene for " + annotationId + " - skipping");
			annotation.setAssertedGene(assertedGene);
		}
		
		if (dto.getInferredAllele() != null) {
			Allele inferredAllele = alleleDAO.find(dto.getInferredAllele());
			if (inferredAllele == null)
				throw new ObjectValidationException(dto, "Invalid inferred allele for " + annotationId + " - skipping");
			annotation.setInferredAllele(inferredAllele);
		}

		if (dto.getAssertedAllele() != null) {
			Allele assertedAllele = alleleDAO.find(dto.getAssertedAllele());
			if (assertedAllele == null)
				throw new ObjectValidationException(dto, "Invalid asserted allele for " + annotationId + " - skipping");
			annotation.setAssertedAllele(assertedAllele);
		}
		return annotation;
	}

	
}
