package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.XbaTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XBATerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class XbaTermService extends BaseOntologyTermService<XBATerm, XbaTermDAO> {

	@Inject XbaTermDAO xbaTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(xbaTermDAO);
	}

}
