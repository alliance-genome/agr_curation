package org.alliancegenome.curation_api.controllers.crud.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.MmoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.MmoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.MmoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class MmoTermCrudController extends BaseOntologyTermController<MmoTermService, MMOTerm, MmoTermDAO> implements MmoTermCrudInterface {

	@Inject
	MmoTermService mmoTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		setService(mmoTermService, MMOTerm.class, config);
	}

}
