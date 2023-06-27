package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.VtTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.VtTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.VTTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.VtTermService;

@RequestScoped
public class VtTermCrudController extends BaseOntologyTermController<VtTermService, VTTerm, VtTermDAO> implements VtTermCrudInterface {

	@Inject
	VtTermService vtTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		setService(vtTermService, VTTerm.class, config);
	}

}
