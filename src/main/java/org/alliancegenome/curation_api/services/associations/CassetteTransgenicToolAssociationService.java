package org.alliancegenome.curation_api.services.associations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.dao.associations.CassetteTransgenicToolAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.associations.CassetteTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteTransgenicToolAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.associations.CassetteTransgenicToolAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.CassetteTransgenicToolAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/** SCRUM-6535: cassette to transgenic tool component. Mirrors ConstructGenomicEntityAssociationService. */
@RequestScoped
public class CassetteTransgenicToolAssociationService extends BaseAssociationDTOCrudService<CassetteTransgenicToolAssociation, CassetteTransgenicToolAssociationDTO, CassetteTransgenicToolAssociationDAO>
	implements BaseUpsertServiceInterface<CassetteTransgenicToolAssociation, CassetteTransgenicToolAssociationDTO> {

	@Inject CassetteTransgenicToolAssociationDAO cassetteTransgenicToolAssociationDAO;
	@Inject CassetteTransgenicToolAssociationValidator cassetteTransgenicToolAssociationValidator;
	@Inject CassetteTransgenicToolAssociationDTOValidator cassetteTransgenicToolAssociationDtoValidator;
	@Inject CassetteDAO cassetteDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(cassetteTransgenicToolAssociationDAO);
	}

	@Transactional
	public ObjectResponse<CassetteTransgenicToolAssociation> upsert(CassetteTransgenicToolAssociation uiEntity) {
		CassetteTransgenicToolAssociation dbEntity = cassetteTransgenicToolAssociationValidator.validateCassetteTransgenicToolAssociation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		dbEntity = cassetteTransgenicToolAssociationDAO.persist(dbEntity);
		return new ObjectResponse<>(dbEntity);
	}

	public ObjectResponse<CassetteTransgenicToolAssociation> validate(CassetteTransgenicToolAssociation uiEntity) {
		CassetteTransgenicToolAssociation association = cassetteTransgenicToolAssociationValidator.validateCassetteTransgenicToolAssociation(uiEntity, true, false);
		return new ObjectResponse<CassetteTransgenicToolAssociation>(association);
	}

	@Override
	@Transactional
	public ObjectResponse<CassetteTransgenicToolAssociation> upsert(CassetteTransgenicToolAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		// The cassettes are reindexed once after the load, not at every association's commit
		cassetteDAO.skipAutomaticIndexing();
		return cassetteTransgenicToolAssociationDtoValidator.validateCassetteTransgenicToolAssociationDTO(dto, dataProvider);
	}

	/** Ids of the associations a given MOD owns, for the load's cleanup pass. */
	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.CASSETTE_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = cassetteTransgenicToolAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	public ObjectResponse<CassetteTransgenicToolAssociation> getAssociation(Long subjectId, String relationName, Long objectId) {
		CassetteTransgenicToolAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("cassetteAssociationSubject.id", subjectId);
		params.put("relation.name", relationName);
		params.put("cassetteTransgenicToolAssociationObject.id", objectId);

		SearchResponse<CassetteTransgenicToolAssociation> resp = cassetteTransgenicToolAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<CassetteTransgenicToolAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}

}
