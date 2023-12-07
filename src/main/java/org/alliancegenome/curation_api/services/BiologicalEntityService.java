package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.services.base.CurieObjectCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BiologicalEntityService extends CurieObjectCrudService<BiologicalEntity, BiologicalEntityDAO> {

	@Inject
	BiologicalEntityDAO biologicalEntityDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(biologicalEntityDAO);
	}
}
