package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.SequenceTargetingReagentFmsDTOValidator;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class SequenceTargetingReagentService extends BaseEntityCrudService<SequenceTargetingReagent, SequenceTargetingReagentDAO> implements BaseUpsertServiceInterface<SequenceTargetingReagent, SequenceTargetingReagentFmsDTO> {

	@Inject SequenceTargetingReagentFmsDTOValidator sqtrDtoValidator;
	@Inject SequenceTargetingReagentDAO sqtrDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(sqtrDAO);
	}

	public SequenceTargetingReagent upsert(SequenceTargetingReagentFmsDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		return sqtrDtoValidator.validateSQTRFmsDTO(dto, dataProvider);
	}
}
