package org.alliancegenome.curation_api.dao.orthology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthology;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneToGeneOrthologyDAO extends BaseSQLDAO<GeneToGeneOrthology> {

	protected GeneToGeneOrthologyDAO() {
		super(GeneToGeneOrthology.class);
	}

}