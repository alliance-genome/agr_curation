package org.alliancegenome.curation_api.services.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.MmusdvTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MMUSDVTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class MmusdvTermService extends BaseOntologyTermService<MMUSDVTerm, MmusdvTermDAO> {

	@Inject
	MmusdvTermDAO mmusdvTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(mmusdvTermDAO);
	}

}
