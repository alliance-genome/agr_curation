package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.MpTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class MpTermService extends BaseOntologyTermService<MPTerm, MpTermDAO> {

	@Inject
	MpTermDAO mpTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(mpTermDAO);
	}

}
