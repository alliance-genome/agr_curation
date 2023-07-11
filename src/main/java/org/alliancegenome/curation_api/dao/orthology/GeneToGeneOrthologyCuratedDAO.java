package org.alliancegenome.curation_api.dao.orthology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyCurated;

@ApplicationScoped
public class GeneToGeneOrthologyCuratedDAO extends BaseSQLDAO<GeneToGeneOrthologyCurated> {

	protected GeneToGeneOrthologyCuratedDAO() {
		super(GeneToGeneOrthologyCurated.class);
	}

}