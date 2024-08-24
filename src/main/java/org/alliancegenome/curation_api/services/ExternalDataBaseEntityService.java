package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.ExternalDataBaseEntityDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPIdFmsDTO;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.ExternalDataBaseEntityFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ExternalDataBaseEntityService extends BaseEntityCrudService<ExternalDataBaseEntity, ExternalDataBaseEntityDAO> implements BaseUpsertServiceInterface<ExternalDataBaseEntity, HTPIdFmsDTO> {
	
	@Inject ExternalDataBaseEntityDAO externalDataBaseEntityDAO;
	@Inject ExternalDataBaseEntityFmsDTOValidator externalDataBaseEntityFmsDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(externalDataBaseEntityDAO);
	}

	public ExternalDataBaseEntity upsert(HTPIdFmsDTO htpIdData, BackendBulkDataProvider backendBulkDataProvider) throws ObjectUpdateException {
		return externalDataBaseEntityFmsDtoValidator.validateExternalDataBaseEntityFmsDTO(htpIdData);
	}

	public List<Long> getDatasetIdsByDataProvider(String dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider);
		List<Long> ids = externalDataBaseEntityDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);
		return ids;
	}
}
