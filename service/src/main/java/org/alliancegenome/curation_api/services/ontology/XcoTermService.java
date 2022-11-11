package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.XcoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XCOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@ApplicationScoped
public class XcoTermService extends BaseOntologyTermService<XCOTerm, XcoTermDAO> {

	@Inject XcoTermDAO xcoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(xcoTermDAO);
	}

}
