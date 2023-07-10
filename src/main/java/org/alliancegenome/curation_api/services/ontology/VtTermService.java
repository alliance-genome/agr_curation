package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.VtTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.VTTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VtTermService extends BaseOntologyTermService<VTTerm, VtTermDAO> {

	@Inject
	VtTermDAO vtTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(vtTermDAO);
	}

}
