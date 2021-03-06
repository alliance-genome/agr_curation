package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.XcoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XcoTerm;

@RequestScoped
public class XcoTermService extends BaseOntologyTermService<XcoTerm, XcoTermDAO> {

	@Inject XcoTermDAO xcoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(xcoTermDAO);
	}

}
