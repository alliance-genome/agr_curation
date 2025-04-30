package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ExpressionAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExpressionAnnotationDAO extends BaseSQLDAO<ExpressionAnnotation> {

		protected ExpressionAnnotationDAO() {
			super(ExpressionAnnotation.class);
		}
}

