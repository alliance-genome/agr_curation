package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.GeneInteractionDAO;
import org.alliancegenome.curation_api.interfaces.crud.GeneInteractionCrudInterface;
import org.alliancegenome.curation_api.model.entities.GeneInteraction;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GeneInteractionService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneInteractionCrudController extends BaseEntityCrudController<GeneInteractionService, GeneInteraction, GeneInteractionDAO>
	implements GeneInteractionCrudInterface {

	@Inject
	GeneInteractionService geneInteractionService;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(geneInteractionService);
	}

	public ObjectResponse<GeneInteraction> get(String identifierString) {
		return geneInteractionService.get(identifierString);
	}
}
