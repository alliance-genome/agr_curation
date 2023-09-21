package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XbsTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XbsTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XBSTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.XbsTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class XbsTermCrudController extends BaseOntologyTermController<XbsTermService, XBSTerm, XbsTermDAO> implements XbsTermCrudInterface {

	@Inject
	XbsTermService xbsTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.getAltNameSpaces().add("xenopus_developmental_stage");
		setService(xbsTermService, XBSTerm.class, config);
	}

}
