package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.GeneMolecularInteractionDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneMolecularInteractionCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.GeneMolecularInteractionExecutor;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GeneMolecularInteractionService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneMolecularInteractionCrudController extends BaseEntityCrudController<GeneMolecularInteractionService, GeneMolecularInteraction, GeneMolecularInteractionDAO>
	implements GeneMolecularInteractionCrudInterface {

	@Inject
	GeneMolecularInteractionService geneMolecularInteractionService;
	@Inject
	GeneMolecularInteractionExecutor geneMolecularInteractionExecutor;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(geneMolecularInteractionService);
	}

	public ObjectResponse<GeneMolecularInteraction> getByIdentifier(String identifierString) {
		return geneMolecularInteractionService.getByIdentifier(identifierString);
	}

	public APIResponse updateInteractions(List<PsiMiTabDTO> interactionData) {
		return geneMolecularInteractionExecutor.runLoad(interactionData);
	}
}
