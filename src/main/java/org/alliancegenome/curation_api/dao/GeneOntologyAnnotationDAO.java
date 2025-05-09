package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneOntologyAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneOntologyAnnotationDAO extends BaseSQLDAO<GeneOntologyAnnotation> {

	protected GeneOntologyAnnotationDAO() {
		super(GeneOntologyAnnotation.class);
	}

}
