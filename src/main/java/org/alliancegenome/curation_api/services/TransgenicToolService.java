package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.ingest.dto.TransgenicToolDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.TransgenicToolValidator;
import org.alliancegenome.curation_api.services.validation.dto.TransgenicToolDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.nilosplace.process_display.ProcessDisplayHelper;

@RequestScoped
public class TransgenicToolService extends SubmittedObjectCrudService<TransgenicTool, TransgenicToolDTO, TransgenicToolDAO> {

	@Inject TransgenicToolDAO transgenicToolDAO;
	@Inject ReferenceService referenceService;
	@Inject TransgenicToolValidator transgenicToolValidator;
	@Inject TransgenicToolDTOValidator transgenicToolDtoValidator;
	@Inject PersonService personService;
	@Inject NoteService noteService;

	private Map<String, Long> transgenicToolIdMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transgenicToolDAO);
	}

	@Override
	public ObjectResponse<TransgenicTool> getByIdentifier(String identifier) {
		TransgenicTool transgenicTool = findByIdentifierString(identifier);
		if (transgenicTool == null) {
			SearchResponse<TransgenicTool> response = findByField("uniqueId", identifier);
			if (response != null) {
				transgenicTool = response.getSingleResult();
			}
		}
		return new ObjectResponse<>(transgenicTool);
	}

	@Override
	@Transactional
	public ObjectResponse<TransgenicTool> update(TransgenicTool uiEntity) {
		TransgenicTool dbEntity = transgenicToolValidator.validateTransgenicToolUpdate(uiEntity);
		return new ObjectResponse<>(transgenicToolDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<TransgenicTool> create(TransgenicTool uiEntity) {
		TransgenicTool dbEntity = transgenicToolValidator.validateTransgenicToolCreate(uiEntity);
		return new ObjectResponse<>(transgenicToolDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<TransgenicTool> upsert(TransgenicToolDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return transgenicToolDtoValidator.validateTransgenicToolDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<TransgenicTool> deleteById(Long id) {
		deprecateOrDelete(id, true, "TransgenicTool DELETE API call", false);
		ObjectResponse<TransgenicTool> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	@Transactional
	public TransgenicTool deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		TransgenicTool transgenicTool = transgenicToolDAO.find(id);
		List<String> deprecationReasons = new ArrayList<>();
		if (transgenicTool != null) {
			if (forceDeprecate) {
				deprecationReasons.add("Deprecation instead of deletion rule applied");
			}
			if (CollectionUtils.isNotEmpty(deprecationReasons)) {
				if (!transgenicTool.getObsolete()) {
					transgenicTool.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					transgenicTool.setDateUpdated(OffsetDateTime.now());
					transgenicTool.setObsolete(true);

					Note deprecationNote = noteService.createDeprecationNote(transgenicTool.getIdentifier(), requestSource, deprecationReasons);
					if (transgenicTool.getRelatedNotes() == null) {
						transgenicTool.setRelatedNotes(new ArrayList<>());
					}
					transgenicTool.getRelatedNotes().add(deprecationNote);

					return transgenicToolDAO.persist(transgenicTool);
				} else {
					return transgenicTool;
				}
			} else {
				transgenicToolDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find TransgenicTool with id: " + id;
			if (throwApiError) {
				ObjectResponse<AffectedGenomicModel> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getTransgenicToolIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> transgenicToolIds = transgenicToolDAO.findIdsByParams(params);
		transgenicToolIds.removeIf(Objects::isNull);

		return transgenicToolIds;
	}

	public Map<String, Long> getTransgenicToolIdMap() {
		if (transgenicToolIdMap.size() > 0) {
			return transgenicToolIdMap;
		}
		transgenicToolIdMap = transgenicToolDAO.getTransgenicToolIdMap();
		return transgenicToolIdMap;
	}

	public Long getIdByModID(String modID) {
		return getTransgenicToolIdMap().get(modID);
	}

	public TransgenicTool getShallowEntity(Long id) {
		if (id == null) {
			return null;
		}
		return transgenicToolDAO.getShallowEntity(TransgenicTool.class, id);
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
