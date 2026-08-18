package org.alliancegenome.curation_api.services.associations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.CassetteGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.associations.CassetteGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.associations.CassetteGenomicEntityAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.CassetteGenomicEntityAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class CassetteGenomicEntityAssociationService extends BaseAssociationDTOCrudService<CassetteGenomicEntityAssociation, CassetteGenomicEntityAssociationDTO, CassetteGenomicEntityAssociationDAO>
	implements BaseUpsertServiceInterface<CassetteGenomicEntityAssociation, CassetteGenomicEntityAssociationDTO> {

	@Inject
	CassetteGenomicEntityAssociationDAO cassetteGenomicEntityAssociationDAO;
	@Inject
	CassetteGenomicEntityAssociationValidator cassetteGenomicEntityAssociationValidator;
	@Inject
	CassetteGenomicEntityAssociationDTOValidator cassetteGenomicEntityAssociationDtoValidator;
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
		setSQLDao(cassetteGenomicEntityAssociationDAO);
	}

	@Transactional
	public ObjectResponse<CassetteGenomicEntityAssociation> upsert(CassetteGenomicEntityAssociation uiEntity) {
		CassetteGenomicEntityAssociation dbEntity = cassetteGenomicEntityAssociationValidator.validateCassetteGenomicEntityAssociation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		dbEntity = cassetteGenomicEntityAssociationDAO.persist(dbEntity);
		return new ObjectResponse<>(dbEntity);
	}

	public ObjectResponse<CassetteGenomicEntityAssociation> validate(CassetteGenomicEntityAssociation uiEntity) {
		CassetteGenomicEntityAssociation aga = cassetteGenomicEntityAssociationValidator.validateCassetteGenomicEntityAssociation(uiEntity, true, false);
		return new ObjectResponse<CassetteGenomicEntityAssociation>(aga);
	}

	@Override
	@Transactional
	public ObjectResponse<CassetteGenomicEntityAssociation> upsert(CassetteGenomicEntityAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return cassetteGenomicEntityAssociationDtoValidator.validateCassetteGenomicEntityAssociationDTO(dto, dataProvider);
	}

	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.CASSETTE_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = cassetteGenomicEntityAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	public ObjectResponse<CassetteGenomicEntityAssociation> getAssociation(Long cassetteId, String relationName, Long genomicEntityId) {
		CassetteGenomicEntityAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("cassetteAssociationSubject.id", cassetteId);
		params.put("relation.name", relationName);
		params.put("cassetteGenomicEntityAssociationObject.id", genomicEntityId);

		SearchResponse<CassetteGenomicEntityAssociation> resp = cassetteGenomicEntityAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<CassetteGenomicEntityAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
}
