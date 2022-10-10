package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
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

	@Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject AlleleDAO alleleDAO;
	@Inject GeneDAO geneDAO;
	@Inject NoteDAO noteDAO;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject AlleleDiseaseAnnotationValidator alleleDiseaseValidator;
	@Inject DiseaseAnnotationService<AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDTO> diseaseAnnotationService;
	@Inject ConditionRelationDAO conditionRelationDAO;
	

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
		
		return alleleDiseaseAnnotationDAO.persist(annotation);
	}
	
	private AlleleDiseaseAnnotation validateAlleleDiseaseAnnotationDTO(AlleleDiseaseAnnotationDTO dto) throws ObjectValidationException {
		AlleleDiseaseAnnotation annotation = new AlleleDiseaseAnnotation();
		Allele allele;
		ObjectResponse<AlleleDiseaseAnnotation> adaResponse = new ObjectResponse<AlleleDiseaseAnnotation>();
		if (StringUtils.isBlank(dto.getSubject())) {
			adaResponse.addErrorMessage("subject", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			allele = alleleDAO.find(dto.getSubject());
			if (allele == null) {
				adaResponse.addErrorMessage("subject", ValidationConstants.INVALID_MESSAGE);
			} else {
				String annotationId = dto.getModEntityId();
				if (StringUtils.isBlank(annotationId)) {
					annotationId = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(allele.getTaxon().getCurie()).getCurieID(dto);
				}
		
				SearchResponse<AlleleDiseaseAnnotation> annotationList = alleleDiseaseAnnotationDAO.findByField("uniqueId", annotationId);
				if (annotationList == null || annotationList.getResults().size() == 0) {
					annotation.setUniqueId(annotationId);
					annotation.setSubject(allele);
				} else {
					annotation = annotationList.getResults().get(0);
				}
			}
		}
		
		ObjectResponse<AlleleDiseaseAnnotation> daResponse = diseaseAnnotationService.validateAnnotationDTO(annotation, dto);
		annotation = daResponse.getEntity();
		adaResponse.addErrorMessages(daResponse.getErrorMessages());
		
		if (StringUtils.isNotEmpty(dto.getDiseaseRelation())) {
			VocabularyTerm diseaseRelation = vocabularyTermDAO.getTermInVocabulary(dto.getDiseaseRelation(), VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY);
			if (diseaseRelation == null)
				adaResponse.addErrorMessage("diseaseRelation", ValidationConstants.INVALID_MESSAGE);
			annotation.setDiseaseRelation(diseaseRelation);
		} else {
			adaResponse.addErrorMessage("diseaseRelation", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (StringUtils.isNotBlank(dto.getInferredGene())) {
			Gene inferredGene = geneDAO.find(dto.getInferredGene());
			if (inferredGene == null)
				adaResponse.addErrorMessage("inferredGene", ValidationConstants.INVALID_MESSAGE);
			annotation.setInferredGene(inferredGene);
		}
		
		if (CollectionUtils.isNotEmpty(dto.getAssertedGenes())) {
			List<Gene> assertedGenes = new ArrayList<>();
			for (String assertedGeneCurie : dto.getAssertedGenes()) {
				Gene assertedGene = geneDAO.find(assertedGeneCurie);
				if (assertedGene == null) {
					adaResponse.addErrorMessage("assertedGenes", ValidationConstants.INVALID_MESSAGE);
					break;
				}
				assertedGenes.add(assertedGene);
			}
			annotation.setAssertedGenes(assertedGenes);
		}
		
		if (adaResponse.hasErrors())
			throw new ObjectValidationException(dto, adaResponse.errorMessagesString());
		
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
