package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.AGMDiseaseAnnotationValidator;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@RequestScoped
public class AGMDiseaseAnnotationService extends BaseDTOCrudService<AGMDiseaseAnnotation, AGMDiseaseAnnotationDTO, AGMDiseaseAnnotationDAO> {

	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	AffectedGenomicModelDAO agmDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	AlleleDAO alleleDAO;
	@Inject
	NoteDAO noteDAO;
	@Inject
	AGMDiseaseAnnotationValidator agmDiseaseValidator;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;
	@Inject
	ConditionRelationDAO conditionRelationDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmDiseaseAnnotationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<AGMDiseaseAnnotation> update(AGMDiseaseAnnotation uiEntity) {
		AGMDiseaseAnnotation dbEntity = agmDiseaseValidator.validateAnnotationUpdate(uiEntity);
		return new ObjectResponse<>(agmDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<AGMDiseaseAnnotation> create(AGMDiseaseAnnotation uiEntity) {
		AGMDiseaseAnnotation dbEntity = agmDiseaseValidator.validateAnnotationCreate(uiEntity);
		return new ObjectResponse<>(agmDiseaseAnnotationDAO.persist(dbEntity));
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
		if (StringUtils.isBlank(dto.getSubject())) {
			throw new ObjectValidationException(dto, "Annotation for " + dto.getObject() + " missing a subject AGM - skipping");
		}

		AffectedGenomicModel agm = agmDAO.find(dto.getSubject());
		if (agm == null) {
			throw new ObjectValidationException(dto, "AGM " + dto.getSubject() + " not found in database - skipping annotation");
		}

		String annotationId = dto.getModEntityId();
		if (StringUtils.isBlank(annotationId)) {
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

		if (StringUtils.isNotBlank(dto.getInferredGene())) {
			Gene inferredGene = geneDAO.find(dto.getInferredGene());
			if (inferredGene == null)
				throw new ObjectValidationException(dto, "Invalid inferred gene for " + annotationId + " - skipping");
			annotation.setInferredGene(inferredGene);
		}

		if (StringUtils.isNotBlank(dto.getAssertedGene())) {
			Gene assertedGene = geneDAO.find(dto.getAssertedGene());
			if (assertedGene == null)
				throw new ObjectValidationException(dto, "Invalid asserted gene for " + annotationId + " - skipping");
			annotation.setAssertedGene(assertedGene);
		}

		if (StringUtils.isNotBlank(dto.getInferredAllele())) {
			Allele inferredAllele = alleleDAO.find(dto.getInferredAllele());
			if (inferredAllele == null)
				throw new ObjectValidationException(dto, "Invalid inferred allele for " + annotationId + " - skipping");
			annotation.setInferredAllele(inferredAllele);
		}

		if (StringUtils.isNotBlank(dto.getAssertedAllele())) {
			Allele assertedAllele = alleleDAO.find(dto.getAssertedAllele());
			if (assertedAllele == null)
				throw new ObjectValidationException(dto, "Invalid asserted allele for " + annotationId + " - skipping");
			annotation.setAssertedAllele(assertedAllele);
		}
		return annotation;
	}

	@Override
	@Transactional
	public ObjectResponse<AGMDiseaseAnnotation> delete(Long id) {
		diseaseAnnotationService.deleteNotes(id);
		AGMDiseaseAnnotation object = dao.remove(id);
		ObjectResponse<AGMDiseaseAnnotation> ret = new ObjectResponse<>();
		return ret;
	}
}
