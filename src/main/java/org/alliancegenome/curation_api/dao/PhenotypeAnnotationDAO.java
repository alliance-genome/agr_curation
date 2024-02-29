package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PhenotypeAnnotationDAO extends BaseSQLDAO<PhenotypeAnnotation> {

	protected PhenotypeAnnotationDAO() {
		super(PhenotypeAnnotation.class);
	}
}
