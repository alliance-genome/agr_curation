package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.FbdvTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.FBDVTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class FbdvTermService extends BaseOntologyTermService<FBDVTerm, FbdvTermDAO> {

	@Inject
	FbdvTermDAO fbdvTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(fbdvTermDAO);
	}

}
