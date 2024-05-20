package org.alliancegenome.curation_api.services;


import org.alliancegenome.curation_api.dao.SpeciesDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class SpeciesService extends BaseEntityCrudService<Species, SpeciesDAO> {

	@Inject
	SpeciesDAO speciesDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(speciesDAO);
	}

}
