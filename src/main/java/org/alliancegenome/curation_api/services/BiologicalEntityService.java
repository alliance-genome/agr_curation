package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@RequestScoped
public class BiologicalEntityService extends BaseEntityCrudService<BiologicalEntity, BiologicalEntityDAO> {

	@Inject
	BiologicalEntityDAO biologicalEntityDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(biologicalEntityDAO);
	}
	
}
