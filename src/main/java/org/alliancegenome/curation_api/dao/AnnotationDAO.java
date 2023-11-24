package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Annotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AnnotationDAO extends BaseSQLDAO<Annotation> {

	protected AnnotationDAO() {
		super(Annotation.class);
	}
}
