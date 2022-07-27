package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class GoTermService extends BaseOntologyTermService<GOTerm, GoTermDAO> {

	@Inject GoTermDAO goTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(goTermDAO);
	}
	
}
