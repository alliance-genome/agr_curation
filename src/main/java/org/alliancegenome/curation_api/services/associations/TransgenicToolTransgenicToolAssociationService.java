package org.alliancegenome.curation_api.services.associations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.dao.associations.TransgenicToolTransgenicToolAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.TransgenicToolTransgenicToolAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.dto.associations.TransgenicToolTransgenicToolAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/** SCRUM-6543: two compatible transgenic tools. Mirrors AgmAgmAssociationService. */
@RequestScoped
public class TransgenicToolTransgenicToolAssociationService extends BaseAssociationDTOCrudService<TransgenicToolTransgenicToolAssociation, TransgenicToolTransgenicToolAssociationDTO, TransgenicToolTransgenicToolAssociationDAO>
	implements BaseUpsertServiceInterface<TransgenicToolTransgenicToolAssociation, TransgenicToolTransgenicToolAssociationDTO> {

	@Inject TransgenicToolTransgenicToolAssociationDAO transgenicToolTransgenicToolAssociationDAO;
	@Inject TransgenicToolTransgenicToolAssociationDTOValidator transgenicToolTransgenicToolAssociationDtoValidator;
	@Inject TransgenicToolDAO transgenicToolDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transgenicToolTransgenicToolAssociationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<TransgenicToolTransgenicToolAssociation> upsert(TransgenicToolTransgenicToolAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		// The transgenic tools are reindexed once after the load, not at every association's commit
		transgenicToolDAO.skipAutomaticIndexing();
		return transgenicToolTransgenicToolAssociationDtoValidator.validateTransgenicToolTransgenicToolAssociationDTO(dto, dataProvider);
	}

	/** Ids of the associations a given MOD owns, for the load's cleanup pass. */
	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.TRANSGENIC_TOOL_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = transgenicToolTransgenicToolAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	public ObjectResponse<TransgenicToolTransgenicToolAssociation> getAssociation(Long subjectId, String relationName, Long objectId) {
		TransgenicToolTransgenicToolAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("transgenicToolAssociationSubject.id", subjectId);
		params.put("relation.name", relationName);
		params.put("transgenicToolTransgenicToolAssociationObject.id", objectId);

		SearchResponse<TransgenicToolTransgenicToolAssociation> resp = transgenicToolTransgenicToolAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<TransgenicToolTransgenicToolAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}

}
