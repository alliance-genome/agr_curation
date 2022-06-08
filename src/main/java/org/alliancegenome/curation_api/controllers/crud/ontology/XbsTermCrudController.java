package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.XbsTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.XbsTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.XBSTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.XbsTermService;

@RequestScoped
public class XbsTermCrudController extends BaseOntologyTermController<XbsTermService, XBSTerm, XbsTermDAO> implements XbsTermCrudInterface {

	@Inject XbsTermService xbsTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.getAltNameSpaces().add("xenopus_developmental_stage");
		setService(xbsTermService, XBSTerm.class, config);
	}

}
