package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.ZecoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.ZecoTermService;

@RequestScoped
public class ZecoTermCrudController extends BaseOntologyTermController<ZecoTermService, ZecoTerm, ZecoTermDAO> implements ZecoTermCrudInterface {

	@Inject ZecoTermService zecoTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadOnlyIRIPrefix("ZECO");
		setService(zecoTermService, ZecoTerm.class, config);
	}

}
