package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.SpeciesDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

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
