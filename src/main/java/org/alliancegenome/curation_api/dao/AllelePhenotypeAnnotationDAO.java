package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AllelePhenotypeAnnotationDAO extends BaseSQLDAO<AllelePhenotypeAnnotation> {

    protected AllelePhenotypeAnnotationDAO() {
		super(AllelePhenotypeAnnotation.class);
	}

}
