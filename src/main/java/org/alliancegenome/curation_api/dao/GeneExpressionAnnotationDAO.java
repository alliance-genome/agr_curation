package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneExpressionAnnotationDAO extends BaseSQLDAO<GeneExpressionAnnotation> {

	protected GeneExpressionAnnotationDAO() {
		super(GeneExpressionAnnotation.class);
	}
}
