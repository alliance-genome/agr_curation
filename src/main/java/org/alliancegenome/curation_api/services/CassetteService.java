package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.ingest.dto.CassetteDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.CassetteValidator;
import org.alliancegenome.curation_api.services.validation.dto.CassetteDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.nilosplace.process_display.ProcessDisplayHelper;

@RequestScoped
public class CassetteService extends SubmittedObjectCrudService<Cassette, CassetteDTO, CassetteDAO> {

	@Inject CassetteDAO cassetteDAO;
	@Inject ReferenceService referenceService;
	@Inject CassetteValidator cassetteValidator;
	@Inject CassetteDTOValidator cassetteDtoValidator;
	@Inject CassetteService cassetteService;
	@Inject PersonService personService;
	@Inject NoteService noteService;

	private Map<String, Long> cassetteIdMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(cassetteDAO);
	}

	@Override
	public ObjectResponse<Cassette> getByIdentifier(String identifier) {
		Cassette cassette = findByIdentifierString(identifier);
		if (cassette == null) {
			SearchResponse<Cassette> response = findByField("uniqueId", identifier);
			if (response != null) {
				cassette = response.getSingleResult();
			}
		}
		return new ObjectResponse<>(cassette);
	}

	@Override
	@Transactional
	public ObjectResponse<Cassette> update(Cassette uiEntity) {
		Cassette dbEntity = cassetteValidator.validateCassetteUpdate(uiEntity);
		return new ObjectResponse<>(cassetteDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<Cassette> create(Cassette uiEntity) {
		Cassette dbEntity = cassetteValidator.validateCassetteCreate(uiEntity);
		return new ObjectResponse<>(cassetteDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<Cassette> upsert(CassetteDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return cassetteDtoValidator.validateCassetteDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<Cassette> deleteById(Long id) {
		deprecateOrDelete(id, true, "Cassette DELETE API call", false);
		ObjectResponse<Cassette> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	@Transactional
	public Cassette deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		Cassette cassette = cassetteDAO.find(id);
		List<String> deprecationReasons = new ArrayList<>();
		if (cassette != null) {
			if (forceDeprecate) {
				deprecationReasons.add("Deprecation instead of deletion rule applied");
			}
			if (CollectionUtils.isNotEmpty(cassette.getCassetteAssociations())) {
				deprecationReasons.add("Cassette has association(s)");
			}
			if (CollectionUtils.isNotEmpty(deprecationReasons)) {
				if (!cassette.getObsolete()) {
					cassette.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					cassette.setDateUpdated(OffsetDateTime.now());
					cassette.setObsolete(true);

					Note deprecationNote = noteService.createDeprecationNote(cassette.getIdentifier(), requestSource, deprecationReasons);
					if (cassette.getRelatedNotes() == null) {
						cassette.setRelatedNotes(new ArrayList<>());
					}
					cassette.getRelatedNotes().add(deprecationNote);

					return cassetteDAO.persist(cassette);
				} else {
					return cassette;
				}
			} else {
				cassetteDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find Cassette with id: " + id;
			if (throwApiError) {
				ObjectResponse<AffectedGenomicModel> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getCassetteIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> cassetteIds = cassetteDAO.findIdsByParams(params);
		cassetteIds.removeIf(Objects::isNull);

		return cassetteIds;
	}

	public Map<String, Long> getCassetteIdMap() {
		if (cassetteIdMap.size() > 0) {
			return cassetteIdMap;
		}
		cassetteIdMap = cassetteDAO.getCassetteIdMap();
		return cassetteIdMap;
	}

	public Long getIdByModID(String modID) {
		return getCassetteIdMap().get(modID);
	}

	public Cassette getShallowEntity(Long id) {
		if (id == null) {
			return null;
		}
		return cassetteDAO.getShallowEntity(Cassette.class, id);
	}

	public void preLoadReferences(Set<String> refList) {
		referenceService.cacheReferences();
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.startProcess("Pre Load References", refList.size());
		for (String curie : refList) {
			referenceService.retrieveFromDbOrLiteratureService(curie);
			ph.progressProcess();
		}
		ph.finishProcess();
	}
}
