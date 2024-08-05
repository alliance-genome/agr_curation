package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.GenomeAssemblyDAO;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GenomeAssemblyService extends BaseEntityCrudService<GenomeAssembly, GenomeAssemblyDAO> {

	@Inject GenomeAssemblyDAO genomeAssemblyDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(genomeAssemblyDAO);
	}

	
}
