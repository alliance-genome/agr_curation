package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.ingest.dto.TransgenicToolDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.TransgenicToolValidator;
import org.alliancegenome.curation_api.services.validation.dto.TransgenicToolDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * SCRUM-6535.
 *
 * upsert drives the bulk load through the DTO validator; create and update drive the curation UI
 * through the curator validator. findByIdentifierString and findIdsByIdentifierString come from
 * SubmittedObjectCrudService and are what the association validators use to resolve a TransgenicTool from a
 * submitted identifier.
 */
@RequestScoped
public class TransgenicToolService extends SubmittedObjectCrudService<TransgenicTool, TransgenicToolDTO, TransgenicToolDAO> {

	@Inject TransgenicToolDAO transgenicToolDAO;
	@Inject TransgenicToolDTOValidator transgenicToolDtoValidator;
	@Inject TransgenicToolValidator transgenicToolValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(transgenicToolDAO);
	}

	@Override
	public ObjectResponse<TransgenicTool> upsert(TransgenicToolDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return transgenicToolDtoValidator.validateTransgenicToolDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<TransgenicTool> update(TransgenicTool uiEntity) {
		TransgenicTool dbEntity = transgenicToolValidator.validateTransgenicToolUpdate(uiEntity);
		return new ObjectResponse<>(transgenicToolDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<TransgenicTool> create(TransgenicTool uiEntity) {
		TransgenicTool dbEntity = transgenicToolValidator.validateTransgenicToolCreate(uiEntity);
		return new ObjectResponse<>(transgenicToolDAO.persist(dbEntity));
	}

}
