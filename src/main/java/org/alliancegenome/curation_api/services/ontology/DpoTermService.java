package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.DpoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DPOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

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
