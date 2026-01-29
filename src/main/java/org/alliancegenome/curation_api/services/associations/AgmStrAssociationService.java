package org.alliancegenome.curation_api.services.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.dao.associations.AgmSequenceTargetingReagentAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.associations.AgmSequenceTargetingReagentAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AgmSequenceTargetingReagentAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.dto.associations.AgmSequenceTargetingReagentAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AgmStrAssociationService extends BaseAssociationDTOCrudService<AgmSequenceTargetingReagentAssociation, AgmSequenceTargetingReagentAssociationDTO, AgmSequenceTargetingReagentAssociationDAO> implements BaseUpsertServiceInterface<AgmSequenceTargetingReagentAssociation, AgmSequenceTargetingReagentAssociationDTO> {

	@Inject AgmSequenceTargetingReagentAssociationDAO agmStrAssociationDAO;
	@Inject AgmSequenceTargetingReagentAssociationDTOValidator agmStrAssociationDtoValidator;
	@Inject AffectedGenomicModelDAO agmDAO;
	@Inject NoteDAO noteDAO;
	@Inject SequenceTargetingReagentDAO strDAO;
	@Inject PersonService personService;
	@Inject PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmStrAssociationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<AgmSequenceTargetingReagentAssociation> upsert(AgmSequenceTargetingReagentAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return agmStrAssociationDtoValidator.validateAgmSequenceTargetingReagentAssociationDTO(dto, dataProvider);
	}

	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.AGM_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = agmStrAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	public ObjectResponse<AgmSequenceTargetingReagentAssociation> getAssociation(Long agmId, String relationName, Long strId) {
		AgmSequenceTargetingReagentAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("agmAssociationSubject.id", agmId);
		params.put("relation.name", relationName);
		params.put("agmSequenceTargetingReagentAssociationObject.id", strId);

		SearchResponse<AgmSequenceTargetingReagentAssociation> resp = agmStrAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<AgmSequenceTargetingReagentAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}

	private void addAssociationToAgm(AgmSequenceTargetingReagentAssociation association) {
		AffectedGenomicModel agm = association.getAgmAssociationSubject();
		List<AgmSequenceTargetingReagentAssociation> currentAssociations = agm.getAgmSequenceTargetingReagentAssociations();
		if (currentAssociations == null) {
			currentAssociations = new ArrayList<>();
			agm.setAgmSequenceTargetingReagentAssociations(currentAssociations);
		}

		List<Long> currentAssociationIds = new ArrayList<>();
		for (AgmSequenceTargetingReagentAssociation aga : currentAssociations) {
			currentAssociationIds.add(aga.getId());
		}

		if (!currentAssociationIds.contains(association.getId())) {
			currentAssociations.add(association);
		}
	}

	private void addAssociationToStr(AgmSequenceTargetingReagentAssociation association) {
		SequenceTargetingReagent str = association.getAgmSequenceTargetingReagentAssociationObject();
		List<AgmSequenceTargetingReagentAssociation> currentAssociations = str.getAgmSequenceTargetingReagentAssociations();
		if (currentAssociations == null) {
			currentAssociations = new ArrayList<>();
			str.setAgmSequenceTargetingReagentAssociations(currentAssociations);
		}

		List<Long> currentAssociationIds = new ArrayList<>();
		for (AgmSequenceTargetingReagentAssociation aga : currentAssociations) {
			currentAssociationIds.add(aga.getId());
		}

		if (!currentAssociationIds.contains(association.getId())) {
			currentAssociations.add(association);
		}
		
	}
}
