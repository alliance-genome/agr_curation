package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseCurieSQLDAO;
import org.alliancegenome.curation_api.model.entities.Antibody;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AntibodyDAO extends BaseCurieSQLDAO<Antibody> {

	protected AntibodyDAO() {
		super(Antibody.class);
	}

}
