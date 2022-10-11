package org.alliancegenome.curation_api.services.validation.dto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class GeneDiseaseAnnotationDTOValidator {

	@Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject AffectedGenomicModelDAO affectedGenomicModelDAO;
	@Inject GeneDAO geneDAO;
	@Inject NoteDAO noteDAO;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject DiseaseAnnotationDTOValidator <GeneDiseaseAnnotation, GeneDiseaseAnnotationDTO> diseaseAnnotationDtoValidator;
	
	public GeneDiseaseAnnotation validateGeneDiseaseAnnotationDTO(GeneDiseaseAnnotationDTO dto) throws ObjectValidationException {
		
		GeneDiseaseAnnotation annotation = new GeneDiseaseAnnotation();
		Gene gene;
		ObjectResponse<GeneDiseaseAnnotation> gdaResponse = new ObjectResponse<GeneDiseaseAnnotation>();
		if (StringUtils.isBlank(dto.getSubject())) {
			gdaResponse.addErrorMessage("subject", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			gene = geneDAO.find(dto.getSubject());
			if (gene == null) {
				gdaResponse.addErrorMessage("subject", ValidationConstants.INVALID_MESSAGE);
			} else {
				String annotationId = dto.getModEntityId();
				if (StringUtils.isBlank(annotationId)) {
					annotationId = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(gene.getTaxon().getCurie()).getCurieID(dto);
				}
		
				SearchResponse<GeneDiseaseAnnotation> annotationList = geneDiseaseAnnotationDAO.findByField("uniqueId", annotationId);
				if (annotationList == null || annotationList.getResults().size() == 0) {
					annotation.setUniqueId(annotationId);
					annotation.setSubject(gene);
				} else {
					annotation = annotationList.getResults().get(0);
				}
			}
		}
		
		AffectedGenomicModel sgdStrainBackground = null;
		if (StringUtils.isNotBlank(dto.getSgdStrainBackground())) {
			sgdStrainBackground = affectedGenomicModelDAO.find(dto.getSgdStrainBackground());
			if (sgdStrainBackground == null || !sgdStrainBackground.getTaxon().getName().startsWith("Saccharomyces cerevisiae")) {
				gdaResponse.addErrorMessage("sgdStrainBackground", ValidationConstants.INVALID_MESSAGE);
			}
		}
		annotation.setSgdStrainBackground(sgdStrainBackground);
		
		ObjectResponse<GeneDiseaseAnnotation> daResponse = diseaseAnnotationDtoValidator.validateAnnotationDTO(annotation, dto);
		annotation = daResponse.getEntity();
		gdaResponse.addErrorMessages(daResponse.getErrorMessages());
		
		if (StringUtils.isNotEmpty(dto.getDiseaseRelation())) {
			VocabularyTerm diseaseRelation = vocabularyTermDAO.getTermInVocabulary(dto.getDiseaseRelation(), VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY);
			if (diseaseRelation == null)
				gdaResponse.addErrorMessage("diseaseRelation", ValidationConstants.INVALID_MESSAGE);
			annotation.setDiseaseRelation(diseaseRelation);
		} else {
			gdaResponse.addErrorMessage("diseaseRelation", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (gdaResponse.hasErrors())
			throw new ObjectValidationException(dto, gdaResponse.errorMessagesString());
		
		return annotation;
	}
}
