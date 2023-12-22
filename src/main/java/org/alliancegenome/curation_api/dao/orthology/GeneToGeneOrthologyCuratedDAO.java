package org.alliancegenome.curation_api.dao.orthology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyCurated;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneToGeneOrthologyCuratedDAO extends BaseSQLDAO<GeneToGeneOrthologyCurated> {

	protected GeneToGeneOrthologyCuratedDAO() {
		super(GeneToGeneOrthologyCurated.class);
	}

}