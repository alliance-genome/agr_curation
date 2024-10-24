package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.SubmittedObjectCrudController;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.interfaces.crud.AlleleCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AlleleExecutor;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AlleleService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleCrudController extends SubmittedObjectCrudController<AlleleService, Allele, AlleleDTO, AlleleDAO> implements AlleleCrudInterface {

	@Inject
	AlleleService alleleService;

	@Inject
	AlleleExecutor alleleExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleService);
	}

	@Override
	public APIResponse updateAlleles(String dataProvider, List<AlleleDTO> alleleData) {
		return alleleExecutor.runLoadApi(alleleService, dataProvider, alleleData);
	}

	public ObjectResponse<Allele> updateDetail(Allele entity) {
		return alleleService.updateDetail(entity);
	}

}
