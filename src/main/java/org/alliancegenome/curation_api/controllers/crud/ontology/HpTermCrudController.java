package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.HpTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.HpTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.HPTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.HpTermService;

@RequestScoped
public class HpTermCrudController extends BaseOntologyTermController<HpTermService, HPTerm, HpTermDAO> implements HpTermCrudInterface {

	@Inject
	HpTermService hpTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadOnlyIRIPrefix("HP");
		setService(hpTermService, HPTerm.class);
	}
}
