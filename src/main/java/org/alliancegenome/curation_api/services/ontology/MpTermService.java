package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.MpTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;

@RequestScoped
public class MpTermService extends BaseOntologyTermService<MPTerm, MpTermDAO> {

	@Inject MpTermDAO mpTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(mpTermDAO);
	}

}
