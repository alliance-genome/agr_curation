package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.MiTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

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
