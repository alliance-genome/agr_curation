package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AnatomicalSiteDAO;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetAnnotationDAO;
import org.alliancegenome.curation_api.dao.HTPExpressionDatasetSampleAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Analysis;
import org.alliancegenome.curation_api.model.entities.AnatomicalSite;
import org.alliancegenome.curation_api.model.entities.BioSampleAge;
import org.alliancegenome.curation_api.model.entities.BioSampleGenomicInformation;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.entities.MicroarraySampleDetails;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AnatomicalSiteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.HTPExpressionDatasetSampleAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CurieMintService;
import org.alliancegenome.curation_api.services.ExternalDataBaseEntityService;
import org.alliancegenome.curation_api.services.ontology.MmoTermService;
import org.alliancegenome.curation_api.services.ontology.ObiTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * SCRUM-6715: direct-submission validator for HTPExpressionDatasetSampleAnnotation, alongside the
 * existing FMS validator (HTPExpressionDatasetSampleAnnotationFmsDTOValidator). Ports that
 * validator's matching key exactly (sample id when present, else data provider + title + the exact
 * set of dataset_ids/anatomical structure curies - see SCRUM-6585) so FMS-loaded and directly
 * submitted records for the same provider are updated, not duplicated.
 */
@RequestScoped
public class HTPExpressionDatasetSampleAnnotationDTOValidator extends AuditedObjectDTOValidator<HTPExpressionDatasetSampleAnnotation, HTPExpressionDatasetSampleAnnotationDTO> {

	@Inject HTPExpressionDatasetSampleAnnotationDAO htpExpressionDatasetSampleAnnotationDAO;
	@Inject HTPExpressionDatasetAnnotationDAO htpExpressionDatasetAnnotationDAO;
	@Inject AnatomicalSiteDAO anatomicalSiteDAO;
	@Inject ExternalDataBaseEntityService externalDataBaseEntityService;
	@Inject ExternalDataBaseEntityDTOValidator externalDataBaseEntityDtoValidator;
	@Inject BioSampleAgeDTOValidator bioSampleAgeDtoValidator;
	@Inject AnatomicalSiteDTOValidator anatomicalSiteDtoValidator;
	@Inject BioSampleGenomicInformationDTOValidator bioSampleGenomicInformationDtoValidator;
	@Inject MicroarraySampleDetailsDTOValidator microarraySampleDetailsDtoValidator;
	@Inject AnalysisDTOValidator analysisDtoValidator;
	@Inject ObiTermService obiTermService;
	@Inject MmoTermService mmoTermService;
	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject CurieMintService curieMintService;

	@Transactional
	public ObjectResponse<HTPExpressionDatasetSampleAnnotation> validateHTPExpressionDatasetSampleAnnotationDTO(HTPExpressionDatasetSampleAnnotationDTO dto, BackendBulkDataProvider backendBulkDataProvider) throws ValidationException {
		response = new ObjectResponse<>();

		HTPExpressionDatasetSampleAnnotation htpSampleAnnotation = matchOrCreate(dto, backendBulkDataProvider);

		htpSampleAnnotation = validateAuditedObjectDTO(htpSampleAnnotation, dto);

		if (StringUtils.isNotBlank(dto.getHtpExpressionSampleTitle())) {
			htpSampleAnnotation.setHtpExpressionSampleTitle(dto.getHtpExpressionSampleTitle());
		}

		htpSampleAnnotation.setHtpExpressionSampleType(validateRequiredOntologyTerm(obiTermService, "htp_expression_sample_type_curie", dto.getHtpExpressionSampleTypeCurie()));
		htpSampleAnnotation.setExpressionAssayUsed(validateRequiredOntologyTerm(mmoTermService, "expression_assay_curie", dto.getExpressionAssayUsedCurie()));

		if (dto.getHtpExpressionSampleAgeDto() != null) {
			ObjectResponse<BioSampleAge> ageResponse = bioSampleAgeDtoValidator.validateBioSampleAgeDTO(dto.getHtpExpressionSampleAgeDto(), htpSampleAnnotation.getHtpExpressionSampleAge());
			if (ageResponse.hasErrors()) {
				response.addErrorMessage("htp_expression_sample_age_dto", ageResponse.errorMessagesString());
			} else {
				htpSampleAnnotation.setHtpExpressionSampleAge(ageResponse.getEntity());
			}
		} else {
			htpSampleAnnotation.setHtpExpressionSampleAge(null);
		}

		// AnatomicalSite is @ManyToMany (not owned exclusively by this entity, no orphanRemoval), so
		// old rows are explicitly deleted after persist below - the same workaround the FMS validator
		// uses, since the relation can't auto-cascade-delete them.
		List<Long> locationIdsToRemove = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(htpSampleAnnotation.getHtpExpressionSampleLocations())) {
			for (AnatomicalSite existingSite : htpSampleAnnotation.getHtpExpressionSampleLocations()) {
				locationIdsToRemove.add(existingSite.getId());
			}
		}
		if (CollectionUtils.isNotEmpty(dto.getHtpExpressionSampleLocationDtos())) {
			List<AnatomicalSite> locations = new ArrayList<>();
			for (AnatomicalSiteDTO locationDto : dto.getHtpExpressionSampleLocationDtos()) {
				ObjectResponse<AnatomicalSite> locationResponse = anatomicalSiteDtoValidator.validateAnatomicalSiteDTO(locationDto);
				if (locationResponse.hasErrors()) {
					response.addErrorMessage("htp_expression_sample_location_dtos", locationResponse.errorMessagesString());
				} else {
					locations.add(locationResponse.getEntity());
				}
			}
			htpSampleAnnotation.setHtpExpressionSampleLocations(locations);
		} else {
			htpSampleAnnotation.setHtpExpressionSampleLocations(null);
		}

		htpSampleAnnotation.setAbundance(handleStringField(dto.getAbundance()));

		if (dto.getGenomicInformationDto() != null) {
			ObjectResponse<BioSampleGenomicInformation> genomicResponse = bioSampleGenomicInformationDtoValidator.validateBioSampleGenomicInformationDTO(dto.getGenomicInformationDto(), htpSampleAnnotation.getGenomicInformation());
			if (genomicResponse.hasErrors()) {
				response.addErrorMessage("genomic_information_dto", genomicResponse.errorMessagesString());
			} else {
				htpSampleAnnotation.setGenomicInformation(genomicResponse.getEntity());
			}
		} else {
			htpSampleAnnotation.setGenomicInformation(null);
		}

		htpSampleAnnotation.setTaxon(validateTaxon("taxon_curie", dto.getTaxonCurie()));
		htpSampleAnnotation.setGeneticSex(validateTermInVocabulary("genetic_sex_name", dto.getGeneticSex(), VocabularyConstants.GENETIC_SEX_VOCABULARY));
		htpSampleAnnotation.setSequencingFormat(validateTermInVocabulary("sequencing_format", dto.getSequencingFormat(), VocabularyConstants.HTP_DATASET_SAMPLE_SEQUENCE_FORMAT_VOCABULARY));
		htpSampleAnnotation.setAssemblyVersions(handleStringListField(dto.getAssemblyVersions()));

		// relatedNotes has orphanRemoval=true - mutate the existing managed collection in place rather
		// than replacing its reference, same fix already applied in AnalysisDTOValidator.
		if (htpSampleAnnotation.getRelatedNotes() != null) {
			htpSampleAnnotation.getRelatedNotes().clear();
		}
		List<Note> validatedNotes = validateNotes(dto.getRelatedNoteDtos(), VocabularyConstants.HTP_DATASET_SAMPLE_NOTE_TYPE_VOCABULARY_TERM_SET);
		if (CollectionUtils.isNotEmpty(validatedNotes)) {
			if (htpSampleAnnotation.getRelatedNotes() == null) {
				htpSampleAnnotation.setRelatedNotes(new ArrayList<>());
			}
			htpSampleAnnotation.getRelatedNotes().addAll(validatedNotes);
		}

		validateDatasetIds(dto, htpSampleAnnotation);

		if (dto.getMicroarraySampleDetailsDto() != null) {
			ObjectResponse<MicroarraySampleDetails> microarrayResponse = microarraySampleDetailsDtoValidator.validateMicroarraySampleDetailsDTO(dto.getMicroarraySampleDetailsDto(), htpSampleAnnotation.getMicroarraySampleDetails());
			if (microarrayResponse.hasErrors()) {
				response.addErrorMessage("microarray_sample_details_dto", microarrayResponse.errorMessagesString());
			} else {
				htpSampleAnnotation.setMicroarraySampleDetails(microarrayResponse.getEntity());
			}
		} else {
			htpSampleAnnotation.setMicroarraySampleDetails(null);
		}

		String submittedDataProvider = dto.getDataProviderDto() == null ? null : dto.getDataProviderDto().getSourceOrganizationAbbreviation();
		if (StringUtils.isBlank(submittedDataProvider)) {
			response.addErrorMessage("data_provider_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else if (!submittedDataProvider.equals(backendBulkDataProvider.sourceOrganization)) {
			// Same reasoning as the sibling HTPExpressionDatasetAnnotationDTOValidator fix: a
			// mismatched data_provider would escape this provider's cleanup tracking
			// (getAnnotationIdsByDataProvider matches on this field) and could be deleted by a
			// different provider's cleanup instead.
			response.addErrorMessage("data_provider_dto", ValidationConstants.INVALID_MESSAGE + " (" + submittedDataProvider + " does not match the load's data provider " + backendBulkDataProvider.sourceOrganization + ")");
		} else {
			ObjectResponse<ImmutablePair<Organization, CrossReference>> dpResponse = validateDataProviderDTO(dto.getDataProviderDto(), htpSampleAnnotation.getDataProviderCrossReference());
			if (dpResponse.hasErrors()) {
				response.addErrorMessage("data_provider_dto", dpResponse.errorMessagesString());
			} else {
				htpSampleAnnotation.setDataProvider(dpResponse.getEntity().getLeft());
				if (dpResponse.getEntity().getRight() != null) {
					// dataProviderCrossReference has no cascade at all, so a brand new CrossReference
					// needs persisting explicitly before it's assigned.
					htpSampleAnnotation.setDataProviderCrossReference(crossReferenceDAO.persist(dpResponse.getEntity().getRight()));
				} else {
					htpSampleAnnotation.setDataProviderCrossReference(null);
				}
			}
		}

		htpSampleAnnotation.setSymbol(handleStringField(dto.getSymbol()));

		if (dto.getAnalysisDto() != null) {
			ObjectResponse<Analysis> analysisResponse = analysisDtoValidator.validateAnalysisDTO(dto.getAnalysisDto(), htpSampleAnnotation.getAnalysis());
			if (analysisResponse.hasErrors()) {
				response.addErrorMessage("analysis_dto", analysisResponse.errorMessagesString());
			} else {
				htpSampleAnnotation.setAnalysis(analysisResponse.getEntity());
			}
		} else {
			htpSampleAnnotation.setAnalysis(null);
		}

		if (dto.getBiologicalReferenceDto() != null) {
			ObjectResponse<ExternalDataBaseEntity> bioRefResponse = externalDataBaseEntityDtoValidator.validateExternalDataBaseEntityDTO(dto.getBiologicalReferenceDto());
			if (bioRefResponse.hasErrors()) {
				response.addErrorMessage("biological_reference_dto", bioRefResponse.errorMessagesString());
			} else {
				htpSampleAnnotation.setBiologicalReference(bioRefResponse.getEntity());
			}
		} else {
			htpSampleAnnotation.setBiologicalReference(null);
		}

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		// SCRUM-6463 — mint an AGRKB curie for a record that has none, same as the FMS path. A record
		// previously loaded via FMS (or an earlier direct submission) already has one by the time it's
		// matched above, so this is a no-op there.
		curieMintService.mintCurieIfAbsent(htpSampleAnnotation);
		HTPExpressionDatasetSampleAnnotation persisted = htpExpressionDatasetSampleAnnotationDAO.persist(htpSampleAnnotation);
		for (Long id : locationIdsToRemove) {
			anatomicalSiteDAO.remove(id);
		}

		response.setEntity(persisted);
		return response;
	}

	private HTPExpressionDatasetSampleAnnotation matchOrCreate(HTPExpressionDatasetSampleAnnotationDTO dto, BackendBulkDataProvider backendBulkDataProvider) {
		boolean hasSampleId = dto.getHtpExpressionSampleDto() != null && StringUtils.isNotBlank(dto.getHtpExpressionSampleDto().getCurie());
		boolean hasSampleTitle = StringUtils.isNotBlank(dto.getHtpExpressionSampleTitle());

		if (!hasSampleId && !hasSampleTitle) {
			response.addErrorMessage("htp_expression_sample_dto", ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + " htp_expression_sample_title");
			return new HTPExpressionDatasetSampleAnnotation();
		}

		if (hasSampleId) {
			ObjectResponse<ExternalDataBaseEntity> sampleResponse = externalDataBaseEntityDtoValidator.validateExternalDataBaseEntityDTO(dto.getHtpExpressionSampleDto());
			if (sampleResponse.hasErrors()) {
				response.addErrorMessage("htp_expression_sample_dto", sampleResponse.errorMessagesString());
				return new HTPExpressionDatasetSampleAnnotation();
			}
			ExternalDataBaseEntity htpExpressionSample = sampleResponse.getEntity();
			Map<String, Object> params = new HashMap<>();
			params.put("htpExpressionSample.id", htpExpressionSample.getId());
			SearchResponse<HTPExpressionDatasetSampleAnnotation> searchResponse = htpExpressionDatasetSampleAnnotationDAO.findByParams(params);
			if (searchResponse == null || searchResponse.getSingleResult() == null) {
				HTPExpressionDatasetSampleAnnotation htpSampleAnnotation = new HTPExpressionDatasetSampleAnnotation();
				htpSampleAnnotation.setHtpExpressionSample(htpExpressionSample);
				return htpSampleAnnotation;
			}
			return searchResponse.getSingleResult();
		}

		// SCRUM-6585 (ported from the FMS validator) — no sample id submitted (the MGI case): the
		// record's identity is (dataProvider, sampleTitle, datasetIds, anatomical structure terms of
		// the sample locations). UBERON slim terms are deliberately excluded: MGI remaps them between
		// releases, which would otherwise churn every affected sample.
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, backendBulkDataProvider.sourceOrganization);
		params.put("htpExpressionSampleTitle", dto.getHtpExpressionSampleTitle());
		Set<String> datasetCuries = new HashSet<>();
		if (CollectionUtils.isNotEmpty(dto.getDatasetIds())) {
			params.put("datasetIds.curie", dto.getDatasetIds().get(0));
			datasetCuries.addAll(dto.getDatasetIds());
		}
		Set<String> structureCuries = new HashSet<>();
		if (CollectionUtils.isNotEmpty(dto.getHtpExpressionSampleLocationDtos())) {
			for (AnatomicalSiteDTO locationDto : dto.getHtpExpressionSampleLocationDtos()) {
				if (StringUtils.isNotBlank(locationDto.getAnatomicalStructureCurie())) {
					structureCuries.add(locationDto.getAnatomicalStructureCurie());
				}
			}
		}
		SearchResponse<HTPExpressionDatasetSampleAnnotation> searchResponse = htpExpressionDatasetSampleAnnotationDAO.findByParams(params);
		if (searchResponse != null) {
			for (HTPExpressionDatasetSampleAnnotation candidate : searchResponse.getResults()) {
				Set<String> candidateDatasetCuries = candidate.getDatasetIds() == null ? Set.of() : candidate.getDatasetIds().stream().map(ExternalDataBaseEntity::getCurie).collect(Collectors.toSet());
				Set<String> candidateStructureCuries = candidate.getHtpExpressionSampleLocations() == null ? Set.of() : candidate.getHtpExpressionSampleLocations().stream().map(AnatomicalSite::getAnatomicalStructure).filter(Objects::nonNull).map(AnatomicalTerm::getCurie).collect(Collectors.toSet());
				if (candidateDatasetCuries.equals(datasetCuries) && candidateStructureCuries.equals(structureCuries)) {
					return candidate;
				}
			}
		}
		return new HTPExpressionDatasetSampleAnnotation();
	}

	// SCRUM-6715: stricter than the FMS path, which only checks each curie resolves to some
	// ExternalDataBaseEntity (silently dropping unresolvable ones) with no check it's backed by a
	// real dataset annotation. This requires an actual HTPExpressionDatasetAnnotation row to exist.
	private void validateDatasetIds(HTPExpressionDatasetSampleAnnotationDTO dto, HTPExpressionDatasetSampleAnnotation htpSampleAnnotation) {
		if (CollectionUtils.isEmpty(dto.getDatasetIds())) {
			response.addErrorMessage("dataset_ids", ValidationConstants.REQUIRED_MESSAGE);
			htpSampleAnnotation.setDatasetIds(null);
			return;
		}

		List<ExternalDataBaseEntity> datasetEntities = new ArrayList<>();
		for (String datasetIdCurie : dto.getDatasetIds()) {
			ExternalDataBaseEntity datasetEntity = externalDataBaseEntityService.findByCurie(datasetIdCurie);
			if (datasetEntity == null) {
				response.addErrorMessage("dataset_ids", ValidationConstants.INVALID_MESSAGE + " (" + datasetIdCurie + ")");
				continue;
			}
			Map<String, Object> datasetParams = new HashMap<>();
			datasetParams.put("htpExpressionDataset.id", datasetEntity.getId());
			SearchResponse<HTPExpressionDatasetAnnotation> datasetSearchResponse = htpExpressionDatasetAnnotationDAO.findByParams(datasetParams);
			if (datasetSearchResponse == null || datasetSearchResponse.getSingleResult() == null) {
				response.addErrorMessage("dataset_ids", ValidationConstants.INVALID_MESSAGE + " (" + datasetIdCurie + " has no matching HTP Expression Dataset Annotation)");
				continue;
			}
			datasetEntities.add(datasetEntity);
		}
		htpSampleAnnotation.setDatasetIds(datasetEntities);
	}

}
