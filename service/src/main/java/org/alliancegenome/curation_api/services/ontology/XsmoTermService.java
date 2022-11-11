package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.XsmoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XSMOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@ApplicationScoped
public class XsmoTermService extends BaseOntologyTermService<XSMOTerm, XsmoTermDAO> {

	@Inject XsmoTermDAO xsmoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(xsmoTermDAO);
	}

}
