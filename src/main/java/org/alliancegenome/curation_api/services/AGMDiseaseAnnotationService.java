package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.AGMDiseaseAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

@RequestScoped
public class AGMDiseaseAnnotationService extends BaseCrudService<AGMDiseaseAnnotation, AGMDiseaseAnnotationDAO> {

	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject AffectedGenomicModelDAO agmDAO;
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
		if (CollectionUtils.isNotEmpty(dbEntity.getConditionRelations())) {
			for (ConditionRelation conditionRelation : dbEntity.getConditionRelations()) {
				conditionRelationDAO.persist(conditionRelation);
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
	
	@Transactional
	public ObjectResponse<AGMDiseaseAnnotation> upsertDTO(AGMDiseaseAnnotationDTO dto) throws ObjectUpdateException {
		AGMDiseaseAnnotation annotation = validateAGMDiseaseAnnotationDTO(dto);
		if (annotation == null) throw new ObjectUpdateException(dto, "Validation Failed");

		annotation = (AGMDiseaseAnnotation) diseaseAnnotationService.upsert(annotation, dto);
		if (annotation != null) {
			agmDiseaseAnnotationDAO.persist(annotation);
		}
		List<CrossReference> refs = annotation.getObject().getCrossReferences();
		return new ObjectResponse<>(annotation);
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
		
		
		return annotation;
	}

	
}
