package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.GeneGeneticInteractionDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneGeneticInteractionCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.GeneGeneticInteractionExecutor;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GeneGeneticInteractionService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneGeneticInteractionCrudController extends BaseEntityCrudController<GeneGeneticInteractionService, GeneGeneticInteraction, GeneGeneticInteractionDAO>
	implements GeneGeneticInteractionCrudInterface {

	@Inject
	GeneGeneticInteractionService geneGeneticInteractionService;
	@Inject
	GeneGeneticInteractionExecutor geneGeneticInteractionExecutor;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(geneGeneticInteractionService);
	}

	public ObjectResponse<GeneGeneticInteraction> getByIdentifier(String identifier) {
		return geneGeneticInteractionService.getByIdentifier(identifier);
	}

	public APIResponse updateInteractions(List<PsiMiTabDTO> interactionData) {
		return geneGeneticInteractionExecutor.runLoadApi(geneGeneticInteractionService, null, interactionData);
	}
	
}
