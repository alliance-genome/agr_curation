package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.XcoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XCOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class XcoTermService extends BaseOntologyTermService<XCOTerm, XcoTermDAO> {

	@Inject
	XcoTermDAO xcoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(xcoTermDAO);
	}

}
