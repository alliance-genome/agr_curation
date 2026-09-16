package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.ingest.dto.TransgenicToolDTO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.dto.TransgenicToolDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6535.
 *
 * Carries no upsert of its own yet; it exists so the association validators can resolve a TransgenicTool
 * from a submitted identifier through findByIdentifierString and findIdsByIdentifierString, both of
 * which SubmittedObjectCrudService provides.
 */
@RequestScoped
public class TransgenicToolService extends SubmittedObjectCrudService<TransgenicTool, TransgenicToolDTO, TransgenicToolDAO> {

	@Inject TransgenicToolDAO transgenicToolDAO;
	@Inject TransgenicToolDTOValidator transgenicToolDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transgenicToolDAO);
	}

	@Override
	public ObjectResponse<TransgenicTool> upsert(TransgenicToolDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return transgenicToolDtoValidator.validateTransgenicToolDTO(dto, dataProvider);
	}

}
