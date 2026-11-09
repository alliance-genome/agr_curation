package org.alliancegenome.curation_api.services.associations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.associations.ConstructCassetteAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.associations.ConstructCassetteAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.ConstructCassetteAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.associations.ConstructCassetteAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.ConstructCassetteAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/** SCRUM-6535: construct to the cassettes that are part of it. Mirrors ConstructGenomicEntityAssociationService. */
@RequestScoped
public class ConstructCassetteAssociationService extends BaseAssociationDTOCrudService<ConstructCassetteAssociation, ConstructCassetteAssociationDTO, ConstructCassetteAssociationDAO>
	implements BaseUpsertServiceInterface<ConstructCassetteAssociation, ConstructCassetteAssociationDTO> {

	@Inject ConstructCassetteAssociationDAO lConstructCassetteAssociationDAO;
	@Inject ConstructCassetteAssociationValidator lConstructCassetteAssociationValidator;
	@Inject ConstructCassetteAssociationDTOValidator lConstructCassetteAssociationDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(lConstructCassetteAssociationDAO);
	}

	@Transactional
	public ObjectResponse<ConstructCassetteAssociation> upsert(ConstructCassetteAssociation uiEntity) {
		ConstructCassetteAssociation dbEntity = lConstructCassetteAssociationValidator.validateConstructCassetteAssociation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		dbEntity = lConstructCassetteAssociationDAO.persist(dbEntity);
		return new ObjectResponse<>(dbEntity);
	}

	public ObjectResponse<ConstructCassetteAssociation> validate(ConstructCassetteAssociation uiEntity) {
		ConstructCassetteAssociation association = lConstructCassetteAssociationValidator.validateConstructCassetteAssociation(uiEntity, true, false);
		return new ObjectResponse<ConstructCassetteAssociation>(association);
	}

	@Override
	@Transactional
	public ObjectResponse<ConstructCassetteAssociation> upsert(ConstructCassetteAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return lConstructCassetteAssociationDtoValidator.validateConstructCassetteAssociationDTO(dto, dataProvider);
	}

	/** Ids of the associations a given MOD owns, for the load's cleanup pass. */
	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.CONSTRUCT_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = lConstructCassetteAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	public ObjectResponse<ConstructCassetteAssociation> getAssociation(Long subjectId, String relationName, Long objectId) {
		ConstructCassetteAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("constructAssociationSubject.id", subjectId);
		params.put("relation.name", relationName);
		params.put("constructCassetteAssociationObject.id", objectId);

		SearchResponse<ConstructCassetteAssociation> resp = lConstructCassetteAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<ConstructCassetteAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}

}
