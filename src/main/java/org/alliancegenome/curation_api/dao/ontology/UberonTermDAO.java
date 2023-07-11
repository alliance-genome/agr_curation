package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.UBERONTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UberonTermDAO extends BaseSQLDAO<UBERONTerm> {

	protected UberonTermDAO() {
		super(UBERONTerm.class);
	}

}
