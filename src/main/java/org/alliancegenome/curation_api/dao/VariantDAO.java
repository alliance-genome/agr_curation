package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Variant;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VariantDAO extends BaseSQLDAO<Variant> {
	
	protected VariantDAO() {
		super(Variant.class);
	}

}
