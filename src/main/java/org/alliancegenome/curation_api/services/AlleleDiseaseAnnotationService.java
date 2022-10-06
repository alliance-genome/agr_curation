package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.AlleleDiseaseAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleDiseaseAnnotationService extends BaseDTOCrudService<AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDTO, AlleleDiseaseAnnotationDAO> {

	@Inject
	AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject
	AlleleDAO alleleDAO;
	@Inject
	GeneDAO geneDAO;
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
		AlleleDiseaseAnnotation dbEntity = alleleDiseaseValidator.validateAnnotationUpdate(uiEntity);
		return new ObjectResponse<>(alleleDiseaseAnnotationDAO.persist(dbEntity));
	}
	
	@Override
	@Transactional
	public ObjectResponse<AlleleDiseaseAnnotation> create(AlleleDiseaseAnnotation uiEntity) {
		AlleleDiseaseAnnotation dbEntity = alleleDiseaseValidator.validateAnnotationCreate(uiEntity);
		return new ObjectResponse<>(alleleDiseaseAnnotationDAO.persist(dbEntity));
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
		if (StringUtils.isBlank(dto.getSubject())) {
			throw new ObjectValidationException(dto, "Annotation for " + dto.getObject() + " missing a subject Allele - skipping");
		}
		
		Allele allele = alleleDAO.find(dto.getSubject());
		if (allele == null) {
			throw new ObjectValidationException(dto, "Allele " + dto.getSubject() + " not found in database - skipping annotation");
		}
		
		String annotationId = dto.getModEntityId();
		if (StringUtils.isBlank(annotationId)) {
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
		
		Gene inferredGene = null;
		if (StringUtils.isNotBlank(dto.getInferredGene())) {
			inferredGene = geneDAO.find(dto.getInferredGene());
			if (inferredGene == null)
				throw new ObjectValidationException(dto, "Invalid inferred gene for " + annotationId + " - skipping");
		}
		annotation.setInferredGene(inferredGene);
		
		if (CollectionUtils.isNotEmpty(dto.getAssertedGenes())) {
			List<Gene> assertedGenes = new ArrayList<>();
			for (String assertedGeneCurie : dto.getAssertedGenes()) {
				Gene assertedGene = geneDAO.find(assertedGeneCurie);
				if (assertedGene == null)
					throw new ObjectValidationException(dto, "Invalid asserted gene for " + annotationId + " - skipping");
				assertedGenes.add(assertedGene);
			}
			annotation.setAssertedGenes(assertedGenes);
		} else {
			annotation.setAssertedGenes(null);
		}
		
		return annotation;
	}

	@Override
	@Transactional
	public ObjectResponse<AlleleDiseaseAnnotation> delete(Long id) {
		diseaseAnnotationService.deleteNotes(id);
		AlleleDiseaseAnnotation object = dao.remove(id);
		ObjectResponse<AlleleDiseaseAnnotation> ret = new ObjectResponse<>();
		return ret;
	}
}
