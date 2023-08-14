package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.ModTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MODTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class ModTermService extends BaseOntologyTermService<MODTerm, ModTermDAO> {

	@Inject
	ModTermDAO modTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(modTermDAO);
	}

}
