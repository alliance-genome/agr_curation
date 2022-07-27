package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseDTOCrudController;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.interfaces.crud.AlleleCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AlleleExecutor;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.AlleleService;

@RequestScoped
public class AlleleCrudController extends BaseDTOCrudController<AlleleService, Allele, AlleleDTO, AlleleDAO> implements AlleleCrudInterface {

	@Inject AlleleService alleleService;
	
	@Inject AlleleExecutor alleleExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleService);
	}

	@Override
	public APIResponse updateAlleles(List<AlleleDTO> alleleData) {
		return alleleExecutor.runLoad(alleleData);
	}

}
