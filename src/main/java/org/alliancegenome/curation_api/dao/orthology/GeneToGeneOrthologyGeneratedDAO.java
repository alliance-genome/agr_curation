package org.alliancegenome.curation_api.dao.orthology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneToGeneOrthologyGeneratedDAO extends BaseSQLDAO<GeneToGeneOrthologyGenerated> {

	protected GeneToGeneOrthologyGeneratedDAO() {
		super(GeneToGeneOrthologyGenerated.class);
	}
}