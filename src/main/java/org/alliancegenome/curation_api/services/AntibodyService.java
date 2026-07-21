package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AntibodyDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Antibody;
import org.alliancegenome.curation_api.model.ingest.dto.AntibodyDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.AntibodyValidator;
import org.alliancegenome.curation_api.services.validation.dto.AntibodyDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.nilosplace.process_display.ProcessDisplayHelper;

@RequestScoped
public class AntibodyService extends SubmittedObjectCrudService<Antibody, AntibodyDTO, AntibodyDAO> {

	@Inject AntibodyDAO antibodyDAO;
	@Inject ReferenceService referenceService;
	@Inject AntibodyValidator antibodyValidator;
	@Inject AntibodyDTOValidator antibodyDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(antibodyDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Antibody> update(Antibody uiEntity) {
		Antibody dbEntity = antibodyValidator.validateAntibodyUpdate(uiEntity);
		return new ObjectResponse<>(antibodyDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<Antibody> create(Antibody uiEntity) {
		Antibody dbEntity = antibodyValidator.validateAntibodyCreate(uiEntity);
		return new ObjectResponse<>(antibodyDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<Antibody> upsert(AntibodyDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return antibodyDtoValidator.validateAntibodyDTO(dto, dataProvider);
	}

	public List<Long> getAntibodyIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		List<Long> antibodyIds = antibodyDAO.findIdsByParams(params);
		antibodyIds.removeIf(Objects::isNull);

		return antibodyIds;
	}

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
