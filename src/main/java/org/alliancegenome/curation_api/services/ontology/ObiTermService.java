package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.ObiTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OBITerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class ObiTermService extends BaseOntologyTermService<OBITerm, ObiTermDAO> {

	@Inject
	ObiTermDAO obiTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(obiTermDAO);
	}

}
