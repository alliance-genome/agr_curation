package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;

@ApplicationScoped
public class DiseaseAnnotationDAO extends BaseSQLDAO<DiseaseAnnotation> {

	protected DiseaseAnnotationDAO() {
		super(DiseaseAnnotation.class);
	}

}
