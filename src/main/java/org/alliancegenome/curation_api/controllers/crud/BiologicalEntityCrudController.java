package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.interfaces.crud.BiologicalEntityCrudInterface;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.ingest.dto.BiologicalEntityDTO;
import org.alliancegenome.curation_api.services.BiologicalEntityService;

@RequestScoped
public class BiologicalEntityCrudController extends BaseEntityCrudController<BiologicalEntityService<BiologicalEntity, BiologicalEntityDTO>, BiologicalEntity, BiologicalEntityDAO> implements BiologicalEntityCrudInterface {

	@Inject BiologicalEntityService<BiologicalEntity, BiologicalEntityDTO> biologicalEntityService;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(biologicalEntityService);
	}

}
