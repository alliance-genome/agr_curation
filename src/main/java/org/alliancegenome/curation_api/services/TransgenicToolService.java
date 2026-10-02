package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
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
import net.nilosplace.process_display.ProcessDisplayHelper;

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
	@Inject ReferenceService referenceService;

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

	/** Ids of the transgenictools a given MOD owns, for the load's cleanup pass. */
	public List<Long> getTransgenicToolIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> ids = transgenicToolDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);

		return ids;
	}

	/**
	 * Resolves every reference the file names up front, so a load does not make one literature
	 * service call per record.
	 */
	public void preLoadReferences(Set<String> refList) {
		referenceService.cacheReferences();
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.startProcess("Pre Load References", refList.size());
		for (String curie : refList) {
			referenceService.retrieveFromDbOrLiteratureService(curie);
			ph.progressProcess();
		}
		ph.finishProcess();
	}
}
