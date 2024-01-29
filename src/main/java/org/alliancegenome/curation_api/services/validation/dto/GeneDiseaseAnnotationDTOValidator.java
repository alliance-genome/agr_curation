package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationRetrievalHelper;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationUniqueIdHelper;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

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
	VocabularyTermService vocabularyTermService;

	public GeneDiseaseAnnotation validateGeneDiseaseAnnotationDTO(GeneDiseaseAnnotationDTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {
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
				annotation = DiseaseAnnotationRetrievalHelper.getCurrentDiseaseAnnotation(annotation, annotationList);
				annotation.setUniqueId(uniqueId);
				annotation.setSubject(gene);
				
				if (dataProvider != null && (dataProvider.name().equals("RGD") || dataProvider.name().equals("HUMAN")) && !gene.getTaxon().getCurie().equals(dataProvider.canonicalTaxonCurie) ||
						!dataProvider.sourceOrganization.equals(gene.getDataProvider().getSourceOrganization().getAbbreviation())) {
					gdaResponse.addErrorMessage("gene_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneCurie() + ") for " + dataProvider.name() + " load");
				}
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
		
		ObjectResponse<GeneDiseaseAnnotation> daResponse = validateDiseaseAnnotationDTO(annotation, dto);
		annotation = daResponse.getEntity();
		gdaResponse.addErrorMessages(daResponse.getErrorMessages());
		
		if (StringUtils.isNotEmpty(dto.getDiseaseRelationName())) {
			VocabularyTerm diseaseRelation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY_TERM_SET, dto.getDiseaseRelationName()).getEntity();
			if (diseaseRelation == null)
				gdaResponse.addErrorMessage("disease_relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDiseaseRelationName() + ")");
			annotation.setRelation(diseaseRelation);
		} else {
			gdaResponse.addErrorMessage("disease_relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (gdaResponse.hasErrors())
			throw new ObjectValidationException(dto, gdaResponse.errorMessagesString());
		
		return annotation;
	}
}
