package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.OntologyTermClosureDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class OntologyTermClosureService extends BaseEntityCrudService<OntologyTermClosure, OntologyTermClosureDAO> {

	@Inject
	OntologyTermClosureDAO ontologyTermClosureDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(ontologyTermClosureDAO);
	}

}
