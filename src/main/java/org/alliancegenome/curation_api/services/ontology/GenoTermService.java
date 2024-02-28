package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.GenoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.GENOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GenoTermService extends BaseOntologyTermService<GENOTerm, GenoTermDAO> {
	
	@Inject
	GenoTermDAO genoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(genoTermDAO);
	}
}
