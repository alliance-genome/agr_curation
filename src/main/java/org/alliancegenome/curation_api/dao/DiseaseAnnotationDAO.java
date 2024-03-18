package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DiseaseAnnotationDAO extends BaseSQLDAO<DiseaseAnnotation> {

	protected DiseaseAnnotationDAO() {
		super(DiseaseAnnotation.class);
	}
}
