package org.alliancegenome.curation_api.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;

@ApplicationScoped
public class GeneExpressionAnnotationDAO  extends BaseSQLDAO<GeneExpressionAnnotation> {

	protected GeneExpressionAnnotationDAO() {
		super(GeneExpressionAnnotation.class);
	}
}
