package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.XpoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XPOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class XpoTermService extends BaseOntologyTermService<XPOTerm, XpoTermDAO> {

	@Inject
	XpoTermDAO xpoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(xpoTermDAO);
	}

}
