package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.WblsTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBLSTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class WblsTermService extends BaseOntologyTermService<WBLSTerm, WblsTermDAO> {

	@Inject
	WblsTermDAO wblsTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(wblsTermDAO);
	}

}
