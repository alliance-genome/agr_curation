package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.ObiTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OBITerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

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
