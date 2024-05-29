package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.SQTRDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.SQTR;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SQTRFmsDTO;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.SQTRFmsDTOValidator;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

public class SQTRService extends SubmittedObjectCrudService<SQTR, SQTRFmsDTO, SQTRDAO> {

	@Inject
	SQTRFmsDTOValidator sqtrDtoValidator;
	@Inject
	SQTRDAO sqtrDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(sqtrDAO);
	}

	public SQTR upsert(SQTRFmsDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		return sqtrDtoValidator.validateSQTRFmsDTO(dto, dataProvider);
	}

	@Transactional
	public void removeOrDeprecateNonUpdated(Long id, String loadDescription) {
		Log.debug("Inside removeOrDeprecateNonUpdated method");
	}

	public List<Long> getIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = sqtrDAO.findFilteredIds(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}
}
