package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.ingest.dto.CassetteDTO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.dto.CassetteDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6535.
 *
 * Carries no upsert of its own yet; it exists so the association validators can resolve a Cassette
 * from a submitted identifier through findByIdentifierString and findIdsByIdentifierString, both of
 * which SubmittedObjectCrudService provides.
 */
@RequestScoped
public class CassetteService extends SubmittedObjectCrudService<Cassette, CassetteDTO, CassetteDAO> {

	@Inject CassetteDAO cassetteDAO;
	@Inject CassetteDTOValidator cassetteDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(cassetteDAO);
	}

	@Override
	public ObjectResponse<Cassette> upsert(CassetteDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return cassetteDtoValidator.validateCassetteDTO(dto, dataProvider);
	}

}
