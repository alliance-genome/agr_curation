package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@RequestScoped
public class GenomicEntityService extends BaseEntityCrudService<GenomicEntity, GenomicEntityDAO> {

	@Inject
	GenomicEntityDAO genomicEntityDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(genomicEntityDAO);
	}
	
}
