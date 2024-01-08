package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.XbsTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XBSTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class XbsTermService extends BaseOntologyTermService<XBSTerm, XbsTermDAO> {

	@Inject
	XbsTermDAO xbsTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(xbsTermDAO);
	}

}
