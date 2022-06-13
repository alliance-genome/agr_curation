package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.FbdvTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.FBdvTerm;

@RequestScoped
public class FbdvTermService extends BaseOntologyTermService<FBdvTerm, FbdvTermDAO> {

	@Inject FbdvTermDAO fbdvTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(fbdvTermDAO);
	}
	
}
