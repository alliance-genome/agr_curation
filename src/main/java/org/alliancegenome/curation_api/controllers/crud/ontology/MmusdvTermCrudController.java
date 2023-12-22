package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.MmusdvTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.MmusdvTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MMUSDVTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.MmusdvTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class MmusdvTermCrudController extends BaseOntologyTermController<MmusdvTermService, MMUSDVTerm, MmusdvTermDAO> implements MmusdvTermCrudInterface {

	@Inject
	MmusdvTermService mmusdvTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.getAltNameSpaces().add("mouse_developmental_stage");
		config.getAltNameSpaces().add("mouse_stages_ontology");
		setService(mmusdvTermService, MMUSDVTerm.class, config);
	}

}
