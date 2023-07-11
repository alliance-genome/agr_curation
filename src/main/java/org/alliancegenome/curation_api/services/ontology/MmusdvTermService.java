package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.MmusdvTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MMUSDVTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

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
