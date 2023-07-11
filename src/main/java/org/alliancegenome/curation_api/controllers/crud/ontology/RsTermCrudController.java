package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.RsTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.RsTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.RSTerm;
import org.alliancegenome.curation_api.services.ontology.RsTermService;

@RequestScoped
public class RsTermCrudController extends BaseOntologyTermController<RsTermService, RSTerm, RsTermDAO> implements RsTermCrudInterface {

	@Inject
	RsTermService rsTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(rsTermService, RSTerm.class);
	}

}
