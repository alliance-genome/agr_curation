package org.alliancegenome.curation_api.services.associations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.associations.CassetteGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.associations.CassetteGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.validation.associations.CassetteGenomicEntityAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.CassetteGenomicEntityAssociationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/** SCRUM-6535: cassette to genomic entity component. Mirrors ConstructGenomicEntityAssociationService. */
@RequestScoped
public class CassetteGenomicEntityAssociationService extends BaseAssociationDTOCrudService<CassetteGenomicEntityAssociation, CassetteGenomicEntityAssociationDTO, CassetteGenomicEntityAssociationDAO>
	implements BaseUpsertServiceInterface<CassetteGenomicEntityAssociation, CassetteGenomicEntityAssociationDTO> {

	@Inject CassetteGenomicEntityAssociationDAO lCassetteGenomicEntityAssociationDAO;
	@Inject CassetteGenomicEntityAssociationValidator lCassetteGenomicEntityAssociationValidator;
	@Inject CassetteGenomicEntityAssociationDTOValidator lCassetteGenomicEntityAssociationDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(lCassetteGenomicEntityAssociationDAO);
	}

	@Transactional
	public ObjectResponse<CassetteGenomicEntityAssociation> upsert(CassetteGenomicEntityAssociation uiEntity) {
		CassetteGenomicEntityAssociation dbEntity = lCassetteGenomicEntityAssociationValidator.validateCassetteGenomicEntityAssociation(uiEntity, true, true);
		if (dbEntity == null) {
			return null;
		}
		dbEntity = lCassetteGenomicEntityAssociationDAO.persist(dbEntity);
		return new ObjectResponse<>(dbEntity);
	}

	public ObjectResponse<CassetteGenomicEntityAssociation> validate(CassetteGenomicEntityAssociation uiEntity) {
		CassetteGenomicEntityAssociation association = lCassetteGenomicEntityAssociationValidator.validateCassetteGenomicEntityAssociation(uiEntity, true, false);
		return new ObjectResponse<CassetteGenomicEntityAssociation>(association);
	}

	@Override
	@Transactional
	public ObjectResponse<CassetteGenomicEntityAssociation> upsert(CassetteGenomicEntityAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return lCassetteGenomicEntityAssociationDtoValidator.validateCassetteGenomicEntityAssociationDTO(dto, dataProvider);
	}

	/** Ids of the associations a given MOD owns, for the load's cleanup pass. */
	public List<Long> getAssociationsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.CASSETTE_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> associationIds = lCassetteGenomicEntityAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	public ObjectResponse<CassetteGenomicEntityAssociation> getAssociation(Long subjectId, String relationName, Long objectId) {
		CassetteGenomicEntityAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put("cassetteAssociationSubject.id", subjectId);
		params.put("relation.name", relationName);
		params.put("cassetteGenomicEntityAssociationObject.id", objectId);

		SearchResponse<CassetteGenomicEntityAssociation> resp = lCassetteGenomicEntityAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<CassetteGenomicEntityAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}

}
