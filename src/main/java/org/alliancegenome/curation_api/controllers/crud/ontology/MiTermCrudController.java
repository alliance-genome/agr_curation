package org.alliancegenome.curation_api.controllers.crud.ontology;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.MiTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.MiTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.services.ontology.MiTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class MiTermCrudController extends BaseOntologyTermController<MiTermService, MITerm, MiTermDAO> implements MiTermCrudInterface {

	@Inject
	MiTermService miTermService;

	@Override
	@PostConstruct
	public void init() {
		setService(miTermService, MITerm.class);
	}

}
