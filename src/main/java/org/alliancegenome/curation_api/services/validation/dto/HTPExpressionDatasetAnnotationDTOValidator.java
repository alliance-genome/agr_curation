package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Analysis;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.HTPExpressionDatasetAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CurieMintService;
import org.alliancegenome.curation_api.services.ExternalDataBaseEntityService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * SCRUM-6714: direct-submission validator for HTPExpressionDatasetAnnotation, alongside the
 * existing FMS validator (HTPExpressionDatasetAnnotationFmsDTOValidator). Keeps the same matching
 * key the FMS path already uses - the resolved htp_expression_dataset ExternalDataBaseEntity id -
 * so records loaded from the FMS are updated, not duplicated, and keep their AGRKB curie.
 */
@RequestScoped
public class HTPExpressionDatasetAnnotationDTOValidator extends AuditedObjectDTOValidator<HTPExpressionDatasetAnnotation, HTPExpressionDatasetAnnotationDTO> {

	@Inject HTPExpressionDatasetAnnotationDAO htpExpressionDatasetAnnotationDAO;
	@Inject ExternalDataBaseEntityService externalDataBaseEntityService;
	@Inject ExternalDataBaseEntityDTOValidator externalDataBaseEntityDtoValidator;
	@Inject AnalysisDTOValidator analysisDtoValidator;
	@Inject CurieMintService curieMintService;
	@Inject CrossReferenceDAO crossReferenceDAO;

	@Transactional
	public ObjectResponse<HTPExpressionDatasetAnnotation> validateHTPExpressionDatasetAnnotationDTO(HTPExpressionDatasetAnnotationDTO dto, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		response = new ObjectResponse<>();

		HTPExpressionDatasetAnnotation htpAnnotation;

		if (dto.getHtpExpressionDatasetDto() == null) {
			response.addErrorMessage("htp_expression_dataset_dto", ValidationConstants.REQUIRED_MESSAGE);
			htpAnnotation = new HTPExpressionDatasetAnnotation();
		} else {
			ObjectResponse<ExternalDataBaseEntity> datasetResponse = externalDataBaseEntityDtoValidator.validateExternalDataBaseEntityDTO(dto.getHtpExpressionDatasetDto());
			if (datasetResponse.hasErrors()) {
				response.addErrorMessage("htp_expression_dataset_dto", datasetResponse.errorMessagesString());
				htpAnnotation = new HTPExpressionDatasetAnnotation();
			} else {
				ExternalDataBaseEntity htpExpressionDataset = datasetResponse.getEntity();
				Map<String, Object> params = new HashMap<>();
				params.put("htpExpressionDataset.id", htpExpressionDataset.getId());
				SearchResponse<HTPExpressionDatasetAnnotation> searchResponse = htpExpressionDatasetAnnotationDAO.findByParams(params);
				if (searchResponse == null || searchResponse.getSingleResult() == null) {
					htpAnnotation = new HTPExpressionDatasetAnnotation();
					htpAnnotation.setHtpExpressionDataset(htpExpressionDataset);
				} else {
					htpAnnotation = searchResponse.getSingleResult();
				}
			}
		}

		htpAnnotation = validateAuditedObjectDTO(htpAnnotation, dto);

		if (StringUtils.isBlank(dto.getName())) {
			response.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);
		}
		htpAnnotation.setName(dto.getName());

		if (CollectionUtils.isNotEmpty(dto.getReferenceCuries())) {
			List<Reference> references = new ArrayList<>();
			for (String referenceCurie : dto.getReferenceCuries()) {
				Reference reference = validateReference("reference_curies", referenceCurie);
				if (reference != null) {
					references.add(reference);
				}
			}
			htpAnnotation.setReferences(references);
		} else {
			htpAnnotation.setReferences(null);
		}

		htpAnnotation.setRelatedNote(validateNote(dto.getRelatedNoteDto(), VocabularyConstants.HTP_DATASET_NOTE_TYPE_VOCABULARY_TERM_SET));

		if (dto.getNumberOfChannels() != null && dto.getNumberOfChannels() != 1 && dto.getNumberOfChannels() != 2) {
			response.addErrorMessage("number_of_channels", ValidationConstants.INVALID_MESSAGE);
		} else {
			htpAnnotation.setNumberOfChannels(dto.getNumberOfChannels());
		}

		if (CollectionUtils.isNotEmpty(dto.getSubSeries())) {
			List<ExternalDataBaseEntity> subSeries = new ArrayList<>();
			for (String subSeriesCurie : dto.getSubSeries()) {
				if (StringUtils.isNotBlank(subSeriesCurie)) {
					ExternalDataBaseEntity subSeriesEntity = externalDataBaseEntityService.findByCurie(subSeriesCurie);
					if (subSeriesEntity == null) {
						response.addErrorMessage("sub_series", ValidationConstants.INVALID_MESSAGE + " (" + subSeriesCurie + ")");
					} else {
						subSeries.add(subSeriesEntity);
					}
				}
			}
			htpAnnotation.setSubSeries(subSeries);
		} else {
			htpAnnotation.setSubSeries(null);
		}

		htpAnnotation.setCategoryTags(validateRequiredTermsInVocabulary("category_tag_names", dto.getCategoryTagNames(), VocabularyConstants.HTP_DATASET_CATEGORY_TAGS_VOCABULARY));

		String submittedDataProvider = dto.getDataProviderDto() == null ? null : dto.getDataProviderDto().getSourceOrganizationAbbreviation();
		if (StringUtils.isBlank(submittedDataProvider)) {
			response.addErrorMessage("data_provider_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else if (!submittedDataProvider.equals(backendBulkDataProvider.sourceOrganization)) {
			// A submitted data_provider that doesn't match the load's own provider would escape this
			// provider's cleanup tracking (getAnnotationIdsByDataProvider matches on this field), and
			// could then be deleted by a different provider's cleanup instead.
			response.addErrorMessage("data_provider_dto", ValidationConstants.INVALID_MESSAGE + " (" + submittedDataProvider + " does not match the load's data provider " + backendBulkDataProvider.sourceOrganization + ")");
		} else {
			ObjectResponse<ImmutablePair<Organization, CrossReference>> dpResponse = validateDataProviderDTO(dto.getDataProviderDto(), htpAnnotation.getDataProviderCrossReference());
			if (dpResponse.hasErrors()) {
				response.addErrorMessage("data_provider_dto", dpResponse.errorMessagesString());
			} else {
				htpAnnotation.setDataProvider(dpResponse.getEntity().getLeft());
				if (dpResponse.getEntity().getRight() != null) {
					// dataProviderCrossReference has no cascade at all, so a brand new CrossReference
					// needs persisting explicitly before it's assigned.
					htpAnnotation.setDataProviderCrossReference(crossReferenceDAO.persist(dpResponse.getEntity().getRight()));
				} else {
					htpAnnotation.setDataProviderCrossReference(null);
				}
			}
		}

		htpAnnotation.setSymbol(handleStringField(dto.getSymbol()));

		if (dto.getAnalysisDto() != null) {
			ObjectResponse<Analysis> analysisResponse = analysisDtoValidator.validateAnalysisDTO(dto.getAnalysisDto(), htpAnnotation.getAnalysis());
			if (analysisResponse.hasErrors()) {
				response.addErrorMessage("analysis_dto", analysisResponse.errorMessagesString());
			} else {
				htpAnnotation.setAnalysis(analysisResponse.getEntity());
			}
		} else {
			htpAnnotation.setAnalysis(null);
		}

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		// SCRUM-6463 — mint an AGRKB curie for a record that has none, same as the FMS path. A record
		// previously loaded via FMS (or an earlier direct submission) already has one by the time it's
		// matched above, so this is a no-op there.
		curieMintService.mintCurieIfAbsent(htpAnnotation);
		response.setEntity(htpExpressionDatasetAnnotationDAO.persist(htpAnnotation));

		return response;
	}

}
