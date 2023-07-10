package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GoTermService extends BaseOntologyTermService<GOTerm, GoTermDAO> {

	@Inject
	GoTermDAO goTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(goTermDAO);
	}

}
