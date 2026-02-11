package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.SequenceTargetingReagentFmsDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class SequenceTargetingReagentService extends SubmittedObjectCrudService<SequenceTargetingReagent, SequenceTargetingReagentFmsDTO, SequenceTargetingReagentDAO> implements BaseUpsertServiceInterface<SequenceTargetingReagent, SequenceTargetingReagentFmsDTO> {

	@Inject SequenceTargetingReagentFmsDTOValidator strDtoValidator;
	@Inject SequenceTargetingReagentDAO strDAO;
	@Inject PersonService personService;
	@Inject NoteService noteService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(strDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<SequenceTargetingReagent> upsert(SequenceTargetingReagentFmsDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return strDtoValidator.validateStrFmsDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public SequenceTargetingReagent deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		SequenceTargetingReagent str = strDAO.find(id);
		List<String> deprecationReasons = new ArrayList<>();
		if (str != null) {
			if (forceDeprecate) {
				deprecationReasons.add("Deprecation instead of deletion rule applied");
			}
			if (CollectionUtils.isNotEmpty(str.getAgmSequenceTargetingReagentAssociations())) {
				deprecationReasons.add("STR has AGM association(s)");
			}
			if (CollectionUtils.isNotEmpty(str.getSequenceTargetingReagentGeneAssociations())) {
				deprecationReasons.add("STR has curated gene association(s)");
			}
			if (CollectionUtils.isNotEmpty(deprecationReasons)) {
				if (!str.getObsolete()) {
					str.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					str.setDateUpdated(OffsetDateTime.now());
					str.setObsolete(true);
					
					Note deprecationNote = noteService.createDeprecationNote(str.getIdentifier(), requestSource, deprecationReasons);
					if (str.getRelatedNotes() == null) {
						str.setRelatedNotes(new ArrayList<>());
					}
					str.getRelatedNotes().add(deprecationNote);
					
					return strDAO.persist(str);
				} else {
					return str;
				}
			} else {
				strDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find SequenceTargetingReagent with id: " + id;
			if (throwApiError) {
				ObjectResponse<Variant> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = strDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}
}
