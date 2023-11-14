package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.XbaTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XBATerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class XbaTermService extends BaseOntologyTermService<XBATerm, XbaTermDAO> {

	@Inject
	XbaTermDAO xbaTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(xbaTermDAO);
	}

}
