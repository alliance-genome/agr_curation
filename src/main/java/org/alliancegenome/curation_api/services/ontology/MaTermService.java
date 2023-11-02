package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.MaTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MATerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class MaTermService extends BaseOntologyTermService<MATerm, MaTermDAO> {

	@Inject
	MaTermDAO maTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(maTermDAO);
	}

}
