package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.FbdvTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.FBDVTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@ApplicationScoped
public class FbdvTermService extends BaseOntologyTermService<FBDVTerm, FbdvTermDAO> {

	@Inject FbdvTermDAO fbdvTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(fbdvTermDAO);
	}
	
}
