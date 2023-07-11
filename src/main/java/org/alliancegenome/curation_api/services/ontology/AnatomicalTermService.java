package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.AnatomicalTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

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
