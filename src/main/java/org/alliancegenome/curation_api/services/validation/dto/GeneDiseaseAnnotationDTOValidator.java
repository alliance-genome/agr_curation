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
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationUniqueIdHelper;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class GeneDiseaseAnnotationDTOValidator extends DiseaseAnnotationDTOValidator {

	@Inject
	GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	NoteDAO noteDAO;
	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;

	public GeneDiseaseAnnotation validateGeneDiseaseAnnotationDTO(GeneDiseaseAnnotationDTO dto) throws ObjectValidationException {
		GeneDiseaseAnnotation annotation = new GeneDiseaseAnnotation();
		Gene gene;

		ObjectResponse<GeneDiseaseAnnotation> gdaResponse = new ObjectResponse<GeneDiseaseAnnotation>();

		ObjectResponse<GeneDiseaseAnnotation> refResponse = validateReference(annotation, dto);
		gdaResponse.addErrorMessages(refResponse.getErrorMessages());
		Reference validatedReference = refResponse.getEntity().getSingleReference();
		String refCurie = validatedReference == null ? null : validatedReference.getCurie();

		if (StringUtils.isBlank(dto.getGeneCurie())) {
			gdaResponse.addErrorMessage("gene_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			gene = geneDAO.find(dto.getGeneCurie());
			if (gene == null) {
				gdaResponse.addErrorMessage("gene_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneCurie() + ")");
			} else {
				String annotationId;
				String identifyingField;
				String uniqueId = DiseaseAnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(dto, dto.getGeneCurie(), refCurie);
				
				if (StringUtils.isNotBlank(dto.getModEntityId())) {
					annotationId = dto.getModEntityId();
					annotation.setModEntityId(annotationId);
					identifyingField = "modEntityId";
				} else if (StringUtils.isNotBlank(dto.getModInternalId())) {
					annotationId = dto.getModInternalId();
					annotation.setModInternalId(annotationId);
					identifyingField = "modInternalId";
				} else {
					annotationId = uniqueId;
					identifyingField = "uniqueId";
				}

				SearchResponse<GeneDiseaseAnnotation> annotationList = geneDiseaseAnnotationDAO.findByField(identifyingField, annotationId);
				if (annotationList != null && annotationList.getResults().size() > 0) {
					annotation = annotationList.getResults().get(0);
				}
				annotation.setUniqueId(uniqueId);
				annotation.setSubject(gene);
			}
		}
		
		annotation.setSingleReference(validatedReference);

		AffectedGenomicModel sgdStrainBackground = null;
		if (StringUtils.isNotBlank(dto.getSgdStrainBackgroundCurie())) {
			sgdStrainBackground = affectedGenomicModelDAO.find(dto.getSgdStrainBackgroundCurie());
			if (sgdStrainBackground == null || !sgdStrainBackground.getTaxon().getName().startsWith("Saccharomyces cerevisiae")) {
				gdaResponse.addErrorMessage("sgd_strain_background_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSgdStrainBackgroundCurie() + ")");
			}
		}
		annotation.setSgdStrainBackground(sgdStrainBackground);
		
		ObjectResponse<GeneDiseaseAnnotation> daResponse = validateAnnotationDTO(annotation, dto);
		annotation = daResponse.getEntity();
		gdaResponse.addErrorMessages(daResponse.getErrorMessages());
		
		if (StringUtils.isNotEmpty(dto.getDiseaseRelationName())) {
			VocabularyTerm diseaseRelation = vocabularyTermDAO.getTermInVocabularyTermSet(VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY_TERM_SET, dto.getDiseaseRelationName());
			if (diseaseRelation == null)
				gdaResponse.addErrorMessage("disease_relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDiseaseRelationName() + ")");
			annotation.setDiseaseRelation(diseaseRelation);
		} else {
			gdaResponse.addErrorMessage("disease_relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (gdaResponse.hasErrors())
			throw new ObjectValidationException(dto, gdaResponse.errorMessagesString());
		
		return annotation;
	}
}
