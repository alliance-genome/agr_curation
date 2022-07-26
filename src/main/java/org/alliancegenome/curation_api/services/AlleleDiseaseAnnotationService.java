package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseDTOCrudService;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.AlleleDiseaseAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class AlleleDiseaseAnnotationService extends BaseDTOCrudService<AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDTO, AlleleDiseaseAnnotationDAO> {

	@Inject
	AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject
	AlleleDAO alleleDAO;
	@Inject
	NoteDAO noteDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	AlleleDiseaseAnnotationValidator alleleDiseaseValidator;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;
	@Inject
	ConditionRelationDAO conditionRelationDAO;
	

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDiseaseAnnotationDAO);
	}
	
	@Override
	@Transactional
	public ObjectResponse<AlleleDiseaseAnnotation> update(AlleleDiseaseAnnotation uiEntity) {
		AlleleDiseaseAnnotation dbEntity = alleleDiseaseValidator.validateAnnotation(uiEntity);
		if (CollectionUtils.isNotEmpty(dbEntity.getRelatedNotes())) {
			for (Note note : dbEntity.getRelatedNotes()) {
				noteDAO.persist(note);
			}
		}
		
		alleleDiseaseAnnotationDAO.persist(dbEntity);
		
		// TODO this return needs to be changed back to the dbEntity in order for new items (notes) to be created properly 
		return new ObjectResponse<>(dbEntity);
	}

	@Transactional
	public AlleleDiseaseAnnotation upsert(AlleleDiseaseAnnotationDTO dto) throws ObjectUpdateException {
		AlleleDiseaseAnnotation annotation = validateAlleleDiseaseAnnotationDTO(dto);
		if (annotation == null) return null;
		
		annotation = (AlleleDiseaseAnnotation) diseaseAnnotationService.upsert(annotation, dto);
		if (annotation != null) {
			alleleDiseaseAnnotationDAO.persist(annotation);
		}
		return annotation;
	}
	
	private AlleleDiseaseAnnotation validateAlleleDiseaseAnnotationDTO(AlleleDiseaseAnnotationDTO dto) throws ObjectValidationException {
		AlleleDiseaseAnnotation annotation;
		if (dto.getSubject() == null) {
			throw new ObjectValidationException(dto, "Annotation for " + dto.getObject() + " missing a subject Allele - skipping");
		}
		
		Allele allele = alleleDAO.find(dto.getSubject());
		if (allele == null) {
			throw new ObjectValidationException(dto, "Allele " + dto.getSubject() + " not found in database - skipping annotation");
		}
		
		String annotationId = dto.getModEntityId();
		if (annotationId == null) {
			annotationId = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(allele.getTaxon().getCurie()).getCurieID(dto);
		}
		SearchResponse<AlleleDiseaseAnnotation> annotationList = alleleDiseaseAnnotationDAO.findByField("uniqueId", annotationId);
		if (annotationList == null || annotationList.getResults().size() == 0) {
			annotation = new AlleleDiseaseAnnotation();
			annotation.setUniqueId(annotationId);
			annotation.setSubject(allele);
		} else {
			annotation = annotationList.getResults().get(0);
		}
		
		annotation = (AlleleDiseaseAnnotation) diseaseAnnotationService.validateAnnotationDTO(annotation, dto);
		
		VocabularyTerm diseaseRelation = vocabularyTermDAO.getTermInVocabulary(dto.getDiseaseRelation(), VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY);
		if (diseaseRelation == null) {
			throw new ObjectValidationException(dto, "Invalid allele disease relation for " + annotationId + " - skipping");
		}
		annotation.setDiseaseRelation(diseaseRelation);
		
		
		return annotation;
	}

}
