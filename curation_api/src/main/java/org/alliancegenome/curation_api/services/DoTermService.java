package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.DoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;

public class DoTermService extends BaseService<DOTerm, DoTermDAO> {

	@Inject DoTermDAO doTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(doTermDAO);
	}

}
