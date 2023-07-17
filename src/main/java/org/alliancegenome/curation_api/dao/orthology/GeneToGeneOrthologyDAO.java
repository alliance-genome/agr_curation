package org.alliancegenome.curation_api.dao.orthology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthology;

@ApplicationScoped
public class GeneToGeneOrthologyDAO extends BaseSQLDAO<GeneToGeneOrthology> {

	protected GeneToGeneOrthologyDAO() {
		super(GeneToGeneOrthology.class);
	}

}