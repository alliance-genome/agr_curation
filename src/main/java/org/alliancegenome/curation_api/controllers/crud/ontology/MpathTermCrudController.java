package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.MpathTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.MpathTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MPATHTerm;
import org.alliancegenome.curation_api.services.ontology.MpathTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class MpathTermCrudController extends BaseOntologyTermController<MpathTermService, MPATHTerm, MpathTermDAO> implements MpathTermCrudInterface {

	@Inject
	MpathTermService mpathTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(mpathTermService, MPATHTerm.class);
	}

}
