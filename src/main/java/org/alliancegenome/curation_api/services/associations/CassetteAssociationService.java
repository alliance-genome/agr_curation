package org.alliancegenome.curation_api.services.associations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.CassetteAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.associations.CassetteAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.associations.CassetteAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.CassetteAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class CassetteAssociationService extends BaseAssociationDTOCrudService<CassetteAssociation, CassetteAssociationDTO, CassetteAssociationDAO>
	implements BaseUpsertServiceInterface<CassetteAssociation, CassetteAssociationDTO> {

	@Inject
	CassetteAssociationDAO cassetteAssociationDAO;
	@Inject
	CassetteAssociationValidator cassetteAssociationValidator;
	@Inject
	CassetteAssociationDTOValidator cassetteAssociationDtoValidator;
	@Inject
	CassetteDAO cassetteDAO;
	@Inject
	GenomicEntityDAO genomicEntityDAO;
	@Inject
	PersonService personService;
	@Inject
	PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(cassetteAssociationDAO);
	}

	@Transactional
	public ObjectResponse<CassetteAssociation> upsert(CassetteAssociation uiEntity) {
		CassetteAssociation dbEntity = cassetteAssociationValidator.validateCassetteAssociation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		dbEntity = cassetteAssociationDAO.persist(dbEntity);
		return new ObjectResponse<>(dbEntity);
	}

	public ObjectResponse<CassetteAssociation> validate(CassetteAssociation uiEntity) {
		CassetteAssociation aga = cassetteAssociationValidator.validateCassetteAssociation(uiEntity, true, false);
		return new ObjectResponse<CassetteAssociation>(aga);
	}

	@Override
	@Transactional
	public ObjectResponse<CassetteAssociation> upsert(CassetteAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return cassetteAssociationDtoValidator.validateCassetteAssociationDTO(dto, dataProvider);
	}

	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.CASSETTE_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = cassetteAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	public ObjectResponse<CassetteAssociation> getAssociation(Long cassetteId, String relationName, Long genomicEntityId) {
		CassetteAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("cassetteAssociationSubject.id", cassetteId);
		params.put("relation.name", relationName);
		params.put("cassetteAssociationObject.id", genomicEntityId);

		SearchResponse<CassetteAssociation> resp = cassetteAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<CassetteAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
}
