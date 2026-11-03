package org.alliancegenome.curation_api.services.associations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.associations.CassetteStrAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.associations.CassetteStrAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteStrAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.associations.CassetteStrAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.CassetteStrAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/** SCRUM-6535: cassette to sequence targeting reagent component. Mirrors ConstructGenomicEntityAssociationService. */
@RequestScoped
public class CassetteStrAssociationService extends BaseAssociationDTOCrudService<CassetteStrAssociation, CassetteStrAssociationDTO, CassetteStrAssociationDAO>
	implements BaseUpsertServiceInterface<CassetteStrAssociation, CassetteStrAssociationDTO> {

	@Inject CassetteStrAssociationDAO lCassetteStrAssociationDAO;
	@Inject CassetteStrAssociationValidator lCassetteStrAssociationValidator;
	@Inject CassetteStrAssociationDTOValidator lCassetteStrAssociationDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(lCassetteStrAssociationDAO);
	}

	@Transactional
	public ObjectResponse<CassetteStrAssociation> upsert(CassetteStrAssociation uiEntity) {
		CassetteStrAssociation dbEntity = lCassetteStrAssociationValidator.validateCassetteStrAssociation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		dbEntity = lCassetteStrAssociationDAO.persist(dbEntity);
		return new ObjectResponse<>(dbEntity);
	}

	public ObjectResponse<CassetteStrAssociation> validate(CassetteStrAssociation uiEntity) {
		CassetteStrAssociation association = lCassetteStrAssociationValidator.validateCassetteStrAssociation(uiEntity, true, false);
		return new ObjectResponse<CassetteStrAssociation>(association);
	}

	@Override
	@Transactional
	public ObjectResponse<CassetteStrAssociation> upsert(CassetteStrAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return lCassetteStrAssociationDtoValidator.validateCassetteStrAssociationDTO(dto, dataProvider);
	}

	/** Ids of the associations a given MOD owns, for the load's cleanup pass. */
	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.CASSETTE_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = lCassetteStrAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	public ObjectResponse<CassetteStrAssociation> getAssociation(Long subjectId, String relationName, Long objectId) {
		CassetteStrAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("cassetteAssociationSubject.id", subjectId);
		params.put("relation.name", relationName);
		params.put("cassetteStrAssociationObject.id", objectId);

		SearchResponse<CassetteStrAssociation> resp = lCassetteStrAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<CassetteStrAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}

}
