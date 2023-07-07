package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.UberonTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.UberonTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.UBERONTerm;
import org.alliancegenome.curation_api.services.ontology.UberonTermService;

@RequestScoped
public class UberonTermCrudController extends BaseOntologyTermController<UberonTermService, UBERONTerm, UberonTermDAO> implements UberonTermCrudInterface {

	@Inject
	UberonTermService uberonTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(uberonTermService, UBERONTerm.class);
	}

}
