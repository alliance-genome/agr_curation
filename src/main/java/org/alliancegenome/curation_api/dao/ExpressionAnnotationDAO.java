package org.alliancegenome.curation_api.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ExpressionAnnotation;

@ApplicationScoped
public class ExpressionAnnotationDAO  extends BaseSQLDAO<ExpressionAnnotation> {

		protected ExpressionAnnotationDAO() {
			super(ExpressionAnnotation.class);
		}
}

