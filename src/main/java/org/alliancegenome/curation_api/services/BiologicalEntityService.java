package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;

@RequestScoped
public class BiologicalEntityService extends BaseCrudService<BiologicalEntity, BiologicalEntityDAO> {

	@Inject
	BiologicalEntityDAO biologicalEntityDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(biologicalEntityDAO);
	}
	
}
