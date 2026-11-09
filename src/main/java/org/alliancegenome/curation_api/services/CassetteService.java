package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
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
import net.nilosplace.process_display.ProcessDisplayHelper;

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
	@Inject ReferenceService referenceService;

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

	/** Ids of the cassettes a given MOD owns, for the load's cleanup pass. */
	public List<Long> getCassetteIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> ids = cassetteDAO.findIdsByParams(params);
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
