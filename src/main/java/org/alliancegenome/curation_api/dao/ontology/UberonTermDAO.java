package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.UBERONTerm;

@ApplicationScoped
public class UberonTermDAO extends BaseSQLDAO<UBERONTerm> {

	protected UberonTermDAO() {
		super(UBERONTerm.class);
	}

}
