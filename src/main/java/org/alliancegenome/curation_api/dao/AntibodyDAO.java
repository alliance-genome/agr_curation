package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Antibody;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AntibodyDAO extends BaseSQLDAO<Antibody> {

	protected AntibodyDAO() {
		super(Antibody.class);
	}

}
