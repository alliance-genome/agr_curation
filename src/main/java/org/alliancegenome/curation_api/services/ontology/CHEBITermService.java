package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.CHEBITermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CHEBITermService extends BaseOntologyTermService<CHEBITerm, CHEBITermDAO> {
	@Inject
	CHEBITermDAO chebiTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(chebiTermDAO);
	}
}
