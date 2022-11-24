package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.InformationContentEntityDAO;
import org.alliancegenome.curation_api.interfaces.crud.InformationContentEntityCrudInterface;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.services.InformationContentEntityService;

@RequestScoped
public class InformationContentEntityCrudController extends BaseEntityCrudController<InformationContentEntityService, InformationContentEntity, InformationContentEntityDAO> implements InformationContentEntityCrudInterface {

	@Inject InformationContentEntityService informationContentEntityService;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(informationContentEntityService);
	}

}
