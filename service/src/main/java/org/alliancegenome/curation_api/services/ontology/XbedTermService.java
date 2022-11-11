package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.XbedTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XBEDTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@ApplicationScoped
public class XbedTermService extends BaseOntologyTermService<XBEDTerm, XbedTermDAO> {

	@Inject XbedTermDAO xbedTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(xbedTermDAO);
	}

}
