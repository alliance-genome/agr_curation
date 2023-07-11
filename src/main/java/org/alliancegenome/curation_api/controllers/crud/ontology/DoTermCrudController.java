package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.DoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.services.ontology.DoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class DoTermCrudController extends BaseOntologyTermController<DoTermService, DOTerm, DoTermDAO> implements DoTermCrudInterface {

	@Inject
	DoTermService doTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(doTermService, DOTerm.class);
	}

}
