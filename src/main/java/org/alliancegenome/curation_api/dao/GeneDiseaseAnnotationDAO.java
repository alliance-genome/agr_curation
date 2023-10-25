package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneDiseaseAnnotationDAO extends BaseSQLDAO<GeneDiseaseAnnotation> {

	protected GeneDiseaseAnnotationDAO() {
		super(GeneDiseaseAnnotation.class);
	}

}
