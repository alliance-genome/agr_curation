package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseCurieSQLDAO;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GenomeAssemblyDAO extends BaseCurieSQLDAO<GenomeAssembly> {

	protected GenomeAssemblyDAO() {
		super(GenomeAssembly.class);
	}

}
