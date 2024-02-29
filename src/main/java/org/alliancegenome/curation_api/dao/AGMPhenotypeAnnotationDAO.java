package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AGMPhenotypeAnnotationDAO extends BaseSQLDAO<AGMPhenotypeAnnotation> {

	protected AGMPhenotypeAnnotationDAO() {
		super(AGMPhenotypeAnnotation.class);
	}

}
