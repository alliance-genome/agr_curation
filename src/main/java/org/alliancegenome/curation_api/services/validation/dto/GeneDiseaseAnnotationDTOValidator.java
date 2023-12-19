package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
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
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationUniqueIdHelper;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneDiseaseAnnotationDTOValidator extends DiseaseAnnotationDTOValidator {

	@Inject
	GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject
	AffectedGenomicModelService affectedGenomicModelService;
	@Inject
	NoteDAO noteDAO;
	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	VocabularyTermService vocabularyTermService;

	public GeneDiseaseAnnotation validateGeneDiseaseAnnotationDTO(GeneDiseaseAnnotationDTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {
		GeneDiseaseAnnotation annotation = new GeneDiseaseAnnotation();
		Gene gene;
		Log.info("GOT HERE 1");
		ObjectResponse<GeneDiseaseAnnotation> gdaResponse = new ObjectResponse<GeneDiseaseAnnotation>();

		Log.info("GOT HERE 2");
		ObjectResponse<GeneDiseaseAnnotation> refResponse = validateReference(annotation, dto);

		Log.info("GOT HERE 3");
		gdaResponse.addErrorMessages(refResponse.getErrorMessages());
		Reference validatedReference = refResponse.getEntity().getSingleReference();

		Log.info("GOT HERE 4");
		String refCurie = validatedReference == null ? null : validatedReference.getCurie();

		Log.info("GOT HERE 5");
		if (StringUtils.isBlank(dto.getGeneIdentifier())) {
			gdaResponse.addErrorMessage("gene_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {

			Log.info("GOT HERE 6");
			gene = geneService.findByIdentifierString(dto.getGeneIdentifier());
			if (gene == null) {
				gdaResponse.addErrorMessage("gene_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneIdentifier() + ")");
			} else {

				Log.info("GOT HERE 7");
				String annotationId;
				String identifyingField;
				String uniqueId = DiseaseAnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(dto, dto.getGeneIdentifier(), refCurie);
				
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

				Log.info("GOT HERE 8");
				SearchResponse<GeneDiseaseAnnotation> annotationList = geneDiseaseAnnotationDAO.findByField(identifyingField, annotationId);
				if (annotationList != null && annotationList.getResults().size() > 0) {
					annotation = annotationList.getResults().get(0);
				}
				annotation.setUniqueId(uniqueId);
				annotation.setSubject(gene);
				
				if (dataProvider != null && (dataProvider.name().equals("RGD") || dataProvider.name().equals("HUMAN")) && !gene.getTaxon().getCurie().equals(dataProvider.canonicalTaxonCurie) ||
						!dataProvider.sourceOrganization.equals(gene.getDataProvider().getSourceOrganization().getAbbreviation())) {
					gdaResponse.addErrorMessage("gene_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneIdentifier() + ") for " + dataProvider.name() + " load");
				}
			}
		}

		Log.info("GOT HERE 9");
		annotation.setSingleReference(validatedReference);

		AffectedGenomicModel sgdStrainBackground = null;
		if (StringUtils.isNotBlank(dto.getSgdStrainBackgroundIdentifier())) {
			sgdStrainBackground = affectedGenomicModelService.findByIdentifierString(dto.getSgdStrainBackgroundIdentifier());
			if (sgdStrainBackground == null || !sgdStrainBackground.getTaxon().getName().startsWith("Saccharomyces cerevisiae")) {
				gdaResponse.addErrorMessage("sgd_strain_background_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSgdStrainBackgroundIdentifier() + ")");
			}
		}
		annotation.setSgdStrainBackground(sgdStrainBackground);

		Log.info("GOT HERE 10");
		ObjectResponse<GeneDiseaseAnnotation> daResponse = validateDiseaseAnnotationDTO(annotation, dto);
		annotation = daResponse.getEntity();
		gdaResponse.addErrorMessages(daResponse.getErrorMessages());

		Log.info("GOT HERE 11");
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
