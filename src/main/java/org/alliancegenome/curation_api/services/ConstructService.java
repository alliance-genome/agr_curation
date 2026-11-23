package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.associations.ConstructCassetteAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.helpers.SubResourceListHelper;
import org.alliancegenome.curation_api.services.validation.ConstructValidator;
import org.alliancegenome.curation_api.services.validation.dto.ConstructDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.nilosplace.process_display.ProcessDisplayHelper;

@RequestScoped
public class ConstructService extends SubmittedObjectCrudService<Construct, ConstructDTO, ConstructDAO> {

	@Inject ConstructDAO constructDAO;
	@Inject ReferenceService referenceService;
	@Inject ConstructValidator constructValidator;
	@Inject ConstructDTOValidator constructDtoValidator;
	@Inject ConstructService constructService;
	@Inject PersonService personService;
	@Inject NoteService noteService;

	private Map<String, Long> constructIdMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(constructDAO);
	}

	@Override
	public ObjectResponse<Construct> getByIdentifier(String identifier) {
		Construct construct = findByIdentifierString(identifier);
		if (construct == null) {
			SearchResponse<Construct> response = findByField("uniqueId", identifier);
			if (response != null) {
				construct = response.getSingleResult();
			}
		}
		return new ObjectResponse<>(construct);
	}

	@Override
	@Transactional
	public ObjectResponse<Construct> update(Construct uiEntity) {
		// ConstructView carries the construct's lists and associations, so this path manages them.
		Construct dbEntity = constructValidator.validateConstructUpdate(uiEntity, true);
		return new ObjectResponse<>(constructDAO.persist(dbEntity));
	}

	@Transactional
	public ObjectResponse<Construct> updateDetail(Construct uiEntity) {
		// ConstructDetailView carries none of the construct's lists or associations, so this path leaves them as they
		// are stored; the detail page writes them through the construct's sub-resources.
		Construct dbEntity = constructValidator.validateConstructUpdate(uiEntity, false);
		return new ObjectResponse<>(constructDAO.persist(dbEntity));
	}

	public ObjectListResponse<ConstructSynonymSlotAnnotation> getSynonyms(Long id) {
		return SubResourceListHelper.listResponse(findConstructOrThrow(id).getConstructSynonyms());
	}

	@Transactional
	public ObjectListResponse<ConstructSynonymSlotAnnotation> updateSynonyms(Long id, List<ConstructSynonymSlotAnnotation> synonyms) {
		Construct construct = findConstructOrThrow(id);
		return SubResourceListHelper.replaceSlotAnnotations(construct, constructValidator.validateSynonymReplacement(construct, synonyms), Construct::getConstructSynonyms, Construct::setConstructSynonyms);
	}

	public ObjectListResponse<String> getSecondaryIds(Long id) {
		return SubResourceListHelper.listResponse(findConstructOrThrow(id).getSecondaryIdentifiers());
	}

	@Transactional
	public ObjectListResponse<String> updateSecondaryIds(Long id, List<String> secondaryIds) {
		Construct construct = findConstructOrThrow(id);
		return SubResourceListHelper.replaceList(construct, constructValidator.validateSecondaryIdReplacement(construct, secondaryIds), Construct::getSecondaryIdentifiers, Construct::setSecondaryIdentifiers);
	}

	public ObjectListResponse<Note> getRelatedNotes(Long id) {
		return SubResourceListHelper.listResponse(findConstructOrThrow(id).getRelatedNotes());
	}

	@Transactional
	public ObjectListResponse<Note> updateRelatedNotes(Long id, List<Note> relatedNotes) {
		Construct construct = findConstructOrThrow(id);
		return SubResourceListHelper.replaceRelatedNotes(construct, constructValidator.validateRelatedNoteReplacement(construct, relatedNotes));
	}

	public ObjectListResponse<Reference> getReferences(Long id) {
		return SubResourceListHelper.listResponse(findConstructOrThrow(id).getReferences());
	}

	@Transactional
	public ObjectListResponse<Reference> updateReferences(Long id, List<Reference> references) {
		Construct construct = findConstructOrThrow(id);
		List<Reference> resolvedReferences = constructValidator.validateReferenceReplacement(construct, references);
		ObjectListResponse<Reference> response = SubResourceListHelper.replaceList(construct, resolvedReferences, Construct::getReferences, Construct::setReferences);

		// The response is serialized after this transaction closes, so each reference's cross references are
		// loaded while it is open.
		resolvedReferences.forEach(reference -> Hibernate.initialize(reference.getCrossReferences()));

		return response;
	}

	public ObjectListResponse<ConstructCassetteAssociation> getCassetteAssociations(Long id) {
		return SubResourceListHelper.listResponse(findConstructOrThrow(id).getConstructCassetteAssociations());
	}

	@Transactional
	public void updateCassetteAssociations(Long id, List<ConstructCassetteAssociation> cassetteAssociations) {
		Construct construct = findConstructOrThrow(id);
		constructValidator.replaceCassetteAssociations(construct, constructValidator.validateCassetteAssociationReplacement(construct, cassetteAssociations));
	}

	private Construct findConstructOrThrow(Long id) {
		Construct construct = constructDAO.find(id);
		if (construct == null) {
			ObjectResponse<Construct> response = new ObjectResponse<>();
			response.addErrorMessage("id", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}
		return construct;
	}

	@Override
	@Transactional
	public ObjectResponse<Construct> create(Construct uiEntity) {
		Construct dbEntity = constructValidator.validateConstructCreate(uiEntity);
		return new ObjectResponse<>(constructDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<Construct> upsert(ConstructDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return constructDtoValidator.validateConstructDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<Construct> deleteById(Long id) {
		deprecateOrDelete(id, true, "Construct DELETE API call", false);
		ObjectResponse<Construct> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	@Transactional
	public Construct deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		Construct construct = constructDAO.find(id);
		List<String> deprecationReasons = new ArrayList<>();
		if (construct != null) {
			if (forceDeprecate) {
				deprecationReasons.add("Deprecation instead of deletion rule applied");
			}
			if (CollectionUtils.isNotEmpty(construct.getConstructGenomicEntityAssociations())) {
				deprecationReasons.add("Construct has genomic entity association(s)");
			}
			if (CollectionUtils.isNotEmpty(deprecationReasons)) {
				if (!construct.getObsolete()) {
					construct.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					construct.setDateUpdated(OffsetDateTime.now());
					construct.setObsolete(true);
					
					Note deprecationNote = noteService.createDeprecationNote(construct.getIdentifier(), requestSource, deprecationReasons);
					if (construct.getRelatedNotes() == null) {
						construct.setRelatedNotes(new ArrayList<>());
					}
					construct.getRelatedNotes().add(deprecationNote);
					
					return constructDAO.persist(construct);
				} else {
					return construct;
				}
			} else {
				constructDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find Construct with id: " + id;
			if (throwApiError) {
				ObjectResponse<AffectedGenomicModel> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getConstructIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> constructIds = constructDAO.findIdsByParams(params);
		constructIds.removeIf(Objects::isNull);

		return constructIds;
	}

	public Map<String, Long> getConstructIdMap() {
		if (constructIdMap.size() > 0) {
			return constructIdMap;
		}
		constructIdMap = constructDAO.getConstructIdMap();
		return constructIdMap;
	}

	public Long getIdByModID(String modID) {
		return getConstructIdMap().get(modID);
	}

	public Construct getShallowEntity(Long id) {
		if (id == null) {
			return null;
		}
		return constructDAO.getShallowEntity(Construct.class, id);
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
