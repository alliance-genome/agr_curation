package org.alliancegenome.curation_api.services.ontology;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.ApoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.APOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ApoTermService extends BaseOntologyTermService<APOTerm, ApoTermDAO> {

	@Inject
	ApoTermDAO apoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(apoTermDAO);
	}

}
