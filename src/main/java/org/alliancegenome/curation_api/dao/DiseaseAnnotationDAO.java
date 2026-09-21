package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseCurieSQLDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DiseaseAnnotationDAO extends BaseCurieSQLDAO<DiseaseAnnotation> {

	protected DiseaseAnnotationDAO() {
		super(DiseaseAnnotation.class);
	}

}
