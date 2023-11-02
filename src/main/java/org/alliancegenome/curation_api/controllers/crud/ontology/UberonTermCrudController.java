package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.UberonTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.UberonTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.UBERONTerm;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.ontology.UberonTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class UberonTermCrudController extends BaseOntologyTermController<UberonTermService, UBERONTerm, UberonTermDAO> implements UberonTermCrudInterface {

	@Inject
	UberonTermService uberonTermService;

	@Override
	@PostConstruct
	public void init() {
		GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
		config.setLoadOnlyIRIPrefix("UBERON");
		setService(uberonTermService, UBERONTerm.class, config);
	}

}
