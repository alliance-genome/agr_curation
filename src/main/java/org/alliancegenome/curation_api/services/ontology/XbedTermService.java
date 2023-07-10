package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.XbedTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XBEDTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class XbedTermService extends BaseOntologyTermService<XBEDTerm, XbedTermDAO> {

	@Inject
	XbedTermDAO xbedTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(xbedTermDAO);
	}

}
