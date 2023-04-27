package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.ZfsTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZFSTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ZfsTermService extends BaseOntologyTermService<ZFSTerm, ZfsTermDAO> {

	@Inject
	ZfsTermDAO zfsTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(zfsTermDAO);
	}

}
