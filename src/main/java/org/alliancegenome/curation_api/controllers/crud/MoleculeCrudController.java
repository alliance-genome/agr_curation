package org.alliancegenome.curation_api.controllers.crud;

import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.MoleculeDAO;
import org.alliancegenome.curation_api.interfaces.crud.MoleculeCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.MoleculeExecutor;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.MoleculeIngestFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.MoleculeService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class MoleculeCrudController extends BaseEntityCrudController<MoleculeService, Molecule, MoleculeDAO> implements MoleculeCrudInterface {

	@Inject
	MoleculeService moleculeService;

	@Inject
	MoleculeExecutor moleculeExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(moleculeService);
	}

	@Override
	public APIResponse updateMolecules(MoleculeIngestFmsDTO moleculeData) {
		BulkLoadFileHistory history = new BulkLoadFileHistory(moleculeData.getData().size());
		moleculeExecutor.runLoad(history, moleculeData);
		history.finishLoad();
		return new LoadHistoryResponce(history);
	}

}
