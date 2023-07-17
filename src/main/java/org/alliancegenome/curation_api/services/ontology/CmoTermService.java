package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.CmoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.CMOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

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
