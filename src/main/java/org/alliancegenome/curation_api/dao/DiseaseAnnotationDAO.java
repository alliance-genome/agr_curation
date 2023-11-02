package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DiseaseAnnotationDAO extends BaseSQLDAO<DiseaseAnnotation> {

	@Inject
	NoteDAO noteDAO;

	protected DiseaseAnnotationDAO() {
		super(DiseaseAnnotation.class);
	}
}
