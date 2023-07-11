package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.FbdvTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.FbdvTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.FBDVTerm;
import org.alliancegenome.curation_api.services.ontology.FbdvTermService;

@RequestScoped
public class FbdvTermCrudController extends BaseOntologyTermController<FbdvTermService, FBDVTerm, FbdvTermDAO> implements FbdvTermCrudInterface {

	@Inject
	FbdvTermService fbdvTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(fbdvTermService, FBDVTerm.class);
	}

}
