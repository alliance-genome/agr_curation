package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.ingest.dto.CassetteDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.CassetteValidator;
import org.alliancegenome.curation_api.services.validation.dto.CassetteDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * SCRUM-6535.
 *
 * upsert drives the bulk load through the DTO validator; create and update drive the curation UI
 * through the curator validator. findByIdentifierString and findIdsByIdentifierString come from
 * SubmittedObjectCrudService and are what the association validators use to resolve a Cassette from a
 * submitted identifier.
 */
@RequestScoped
public class CassetteService extends SubmittedObjectCrudService<Cassette, CassetteDTO, CassetteDAO> {

	@Inject CassetteDAO cassetteDAO;
	@Inject CassetteDTOValidator cassetteDtoValidator;
	@Inject CassetteValidator cassetteValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(cassetteDAO);
	}

	@Override
	public ObjectResponse<Cassette> upsert(CassetteDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return cassetteDtoValidator.validateCassetteDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<Cassette> update(Cassette uiEntity) {
		Cassette dbEntity = cassetteValidator.validateCassetteUpdate(uiEntity);
		return new ObjectResponse<>(cassetteDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<Cassette> create(Cassette uiEntity) {
		Cassette dbEntity = cassetteValidator.validateCassetteCreate(uiEntity);
		return new ObjectResponse<>(cassetteDAO.persist(dbEntity));
	}

}
