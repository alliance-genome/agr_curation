package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.DpoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DPOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class DpoTermService extends BaseOntologyTermService<DPOTerm, DpoTermDAO> {

	@Inject
	DpoTermDAO dpoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(dpoTermDAO);
	}

}
