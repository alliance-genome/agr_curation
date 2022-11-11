package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.MaTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.MaTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MATerm;
import org.alliancegenome.curation_api.services.ontology.MaTermService;

@RequestScoped
public class MaTermCrudController extends BaseOntologyTermController<MaTermService, MATerm, MaTermDAO> implements MaTermCrudInterface {

	@Inject MaTermService maTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(maTermService, MATerm.class);
	}

}
