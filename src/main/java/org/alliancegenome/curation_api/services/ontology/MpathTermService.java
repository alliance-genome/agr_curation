package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.MpathTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MPATHTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class MpathTermService extends BaseOntologyTermService<MPATHTerm, MpathTermDAO> {

	@Inject
	MpathTermDAO mpathTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(mpathTermDAO);
	}

}
