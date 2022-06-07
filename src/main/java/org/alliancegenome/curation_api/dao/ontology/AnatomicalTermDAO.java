package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;

@ApplicationScoped
public class AnatomicalTermDAO extends BaseSQLDAO<AnatomicalTerm> {

	protected AnatomicalTermDAO() {
		super(AnatomicalTerm.class);
	}
	
}