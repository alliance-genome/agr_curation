package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.interfaces.crud.BiologicalEntityCrudInterface;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.services.BiologicalEntityService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BiologicalEntityCrudController extends BaseEntityCrudController<BiologicalEntityService, BiologicalEntity, BiologicalEntityDAO> implements BiologicalEntityCrudInterface {

	@Inject
	BiologicalEntityService biologicalEntityService;

	@Override
	@PostConstruct
	protected void init() {
		setService(biologicalEntityService);
	}

}
