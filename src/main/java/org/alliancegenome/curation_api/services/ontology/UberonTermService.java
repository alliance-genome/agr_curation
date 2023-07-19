package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.UberonTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.UBERONTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@RequestScoped
public class UberonTermService extends BaseOntologyTermService<UBERONTerm, UberonTermDAO> {

	@Inject
	UberonTermDAO uberonTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(uberonTermDAO);
	}

}
