package org.alliancegenome.curation_api.services.ontology;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.MmoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class MmoTermService extends BaseOntologyTermService<MMOTerm, MmoTermDAO> {

	@Inject
	MmoTermDAO mmoTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(mmoTermDAO);
	}

}
