package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.ingest.dto.GenomicEntityDTO;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GenomicEntityService extends SubmittedObjectCrudService<GenomicEntity, GenomicEntityDTO, GenomicEntityDAO> {

	@Inject GenomicEntityDAO genomicEntityDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(genomicEntityDAO);
	}

	@Override
	public GenomicEntity upsert(GenomicEntityDTO dto) throws ObjectUpdateException {
		return null;
	}

	@Override
	public GenomicEntity upsert(GenomicEntityDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		return null;
	}

	@Override
	public GenomicEntity deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean deprecate) {
		return null;
	}

}
