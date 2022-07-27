package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class DoTermService extends BaseOntologyTermService<DOTerm, DoTermDAO> {

	@Inject DoTermDAO doTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(doTermDAO);
	}
	
}
