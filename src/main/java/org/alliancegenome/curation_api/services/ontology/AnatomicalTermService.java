package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.AnatomicalTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AnatomicalTermService extends BaseOntologyTermService<AnatomicalTerm, AnatomicalTermDAO> {

	@Inject
	AnatomicalTermDAO anatomicalTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(anatomicalTermDAO);
	}

}
