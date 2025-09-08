package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.TranscriptDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.Gff3DtoValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class TranscriptService extends BaseEntityCrudService<Transcript, TranscriptDAO> {

	@Inject TranscriptDAO transcriptDAO;
	@Inject PersonService personService;
	@Inject Gff3DtoValidator gff3DtoValidator;
	@Inject NoteService noteService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transcriptDAO);
	}

	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD") || StringUtils.equals(dataProvider.sourceOrganization, "XB")) {
			params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> ids = transcriptDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}

	public ObjectResponse<Transcript> deleteByIdentifier(String identifierString) {
		Transcript transcript = findByAlternativeFields(List.of("curie", "primaryExternalId", "modInternalId"), identifierString);
		if (transcript != null) {
			transcriptDAO.remove(transcript.getId());
		}
		ObjectResponse<Transcript> ret = new ObjectResponse<>(transcript);
		return ret;
	}
	
	@Override
	@Transactional
	public Transcript deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		Transcript transcript = transcriptDAO.find(id);
		List<String> deprecationReasons = new ArrayList<>();
		if (transcript != null) {
			if (forceDeprecate) {
				deprecationReasons.add("Deprecation instead of deletion rule applied");
			}
			if (transcriptDAO.hasReferencingPredictedVariantConsequences(id)) {
				deprecationReasons.add("Transcript has predicted variant consequence(s)");
			}
			if (CollectionUtils.isNotEmpty(deprecationReasons)) {
				if (!transcript.getObsolete()) {
					transcript.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					transcript.setDateUpdated(OffsetDateTime.now());
					transcript.setObsolete(true);
					
					Note deprecationNote = noteService.createDeprecationNote(transcript.getIdentifier(), requestSource, deprecationReasons);
					if (transcript.getRelatedNotes() == null) {
						transcript.setRelatedNotes(new ArrayList<>());
					}
					transcript.getRelatedNotes().add(deprecationNote);
					
					return transcriptDAO.persist(transcript);
				} else {
					return transcript;
				}
			} else {
				transcriptDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find Transcript with id: " + id;
			if (throwApiError) {
				ObjectResponse<Transcript> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

}
