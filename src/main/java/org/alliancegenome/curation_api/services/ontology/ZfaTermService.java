package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.ZfaTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZFATerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ZfaTermService extends BaseOntologyTermService<ZFATerm, ZfaTermDAO> {

	@Inject
	ZfaTermDAO zfaTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(zfaTermDAO);
	}

}
