package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseDTOCrudController;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.interfaces.crud.ConstructCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.ConstructExecutor;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.ConstructService;

@RequestScoped
public class ConstructCrudController extends BaseDTOCrudController<ConstructService, Construct, ConstructDTO, ConstructDAO>
	implements ConstructCrudInterface {

	@Inject
	ConstructService constructService;
	@Inject
	ConstructExecutor constructExecutor;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(constructService);
	}

	@Override
	public APIResponse updateConstructs(String dataProvider, List<ConstructDTO> annotations) {
		return constructExecutor.runLoad(dataProvider, annotations);
	}
}