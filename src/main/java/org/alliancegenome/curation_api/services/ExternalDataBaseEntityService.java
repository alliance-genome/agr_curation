package org.alliancegenome.curation_api.services;

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
}
