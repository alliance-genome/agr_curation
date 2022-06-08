package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.DoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.services.ontology.DoTermService;

@RequestScoped
public class DoTermCrudController extends BaseOntologyTermController<DoTermService, DOTerm, DoTermDAO> implements DoTermCrudInterface {

	@Inject DoTermService doTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(doTermService, DOTerm.class);
	}

}
