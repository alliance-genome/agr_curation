package org.alliancegenome.curation_api.services.ontology;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.UberonTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.UBERONTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

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
