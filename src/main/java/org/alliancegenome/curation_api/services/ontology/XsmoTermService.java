package org.alliancegenome.curation_api.services.ontology;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.XsmoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XSMOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class XsmoTermService extends BaseOntologyTermService<XSMOTerm, XsmoTermDAO> {

	@Inject
	XsmoTermDAO xsmoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(xsmoTermDAO);
	}

}
