package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.CmoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.CMOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CmoTermService extends BaseOntologyTermService<CMOTerm, CmoTermDAO> {

	@Inject
	CmoTermDAO cmoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(cmoTermDAO);
	}

}
