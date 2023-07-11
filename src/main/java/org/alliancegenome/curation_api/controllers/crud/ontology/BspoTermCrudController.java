package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.BspoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.BspoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.BSPOTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.BspoTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BspoTermCrudController extends BaseOntologyTermController<BspoTermService, BSPOTerm, BspoTermDAO> implements BspoTermCrudInterface {

	@Inject
	BspoTermService bspoTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadOnlyIRIPrefix("BSPO");
		setService(bspoTermService, BSPOTerm.class, config);
	}

}
