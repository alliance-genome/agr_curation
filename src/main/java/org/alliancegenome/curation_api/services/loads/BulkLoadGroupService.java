package org.alliancegenome.curation_api.services.loads;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkLoadGroupDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadGroup;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class BulkLoadGroupService extends BaseEntityCrudService<BulkLoadGroup, BulkLoadGroupDAO> {

	@Inject
	BulkLoadGroupDAO bulkLoadGroupDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkLoadGroupDAO);
	}
}