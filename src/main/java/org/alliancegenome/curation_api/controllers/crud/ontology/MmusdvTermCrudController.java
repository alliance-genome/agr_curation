package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.MmusdvTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.MmusdvTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MmusDvTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.MmusdvTermService;

@RequestScoped
public class MmusdvTermCrudController extends BaseOntologyTermController<MmusdvTermService, MmusDvTerm, MmusdvTermDAO> implements MmusdvTermCrudInterface {

	@Inject MmusdvTermService mmusdvTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.getAltNameSpaces().add("mouse_developmental_stage");
		config.getAltNameSpaces().add("mouse_stages_ontology");
		setService(mmusdvTermService, MmusDvTerm.class, config);
	}

}
