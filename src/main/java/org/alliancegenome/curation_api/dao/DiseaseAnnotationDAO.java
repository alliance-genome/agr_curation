package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotationCurated;

public class DiseaseAnnotationDAO extends BaseSQLDAO<DiseaseAnnotationCurated> {

	protected DiseaseAnnotationDAO() {
		super(DiseaseAnnotationCurated.class);
	}

}
