package org.alliancegenome.curation_api.dao;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DiseaseAnnotationDAO extends BaseSQLDAO<DiseaseAnnotation> {

	@Inject
	NoteDAO noteDAO;

	protected DiseaseAnnotationDAO() {
		super(DiseaseAnnotation.class);
	}
}
