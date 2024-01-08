package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XbaTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XbaTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XBATerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.XbaTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class XbaTermCrudController extends BaseOntologyTermController<XbaTermService, XBATerm, XbaTermDAO> implements XbaTermCrudInterface {

	@Inject
	XbaTermService xbaTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.getAltNameSpaces().add("xenopus_anatomy");
		config.getAltNameSpaces().add("xenopus_anatomy_in_vitro");
		setService(xbaTermService, XBATerm.class);
	}

}
