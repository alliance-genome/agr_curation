package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.MaTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MATerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@ApplicationScoped
public class MaTermService extends BaseOntologyTermService<MATerm, MaTermDAO> {

	@Inject MaTermDAO maTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(maTermDAO);
	}
	
}
