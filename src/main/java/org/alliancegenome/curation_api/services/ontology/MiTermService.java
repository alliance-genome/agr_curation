package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.dao.ontology.MiTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class MiTermService extends BaseOntologyTermService<MITerm, MiTermDAO> {

	@Inject
	MiTermDAO miTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(miTermDAO);
	}

}
