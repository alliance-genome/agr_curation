package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GenePhenotypeAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GenePhenotypeAnnotationDAO extends BaseSQLDAO<GenePhenotypeAnnotation> {

	protected GenePhenotypeAnnotationDAO() {
		super(GenePhenotypeAnnotation.class);
	}

}
