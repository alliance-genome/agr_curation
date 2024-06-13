package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.SequenceTargetingReagentFmsDTOValidator;

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
	@Transactional
	public SequenceTargetingReagent upsert(SequenceTargetingReagentFmsDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		SequenceTargetingReagent sqtr = sqtrDtoValidator.validateSQTRFmsDTO(dto, dataProvider);
		return sqtrDAO.persist(sqtr);

	}
}
