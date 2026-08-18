package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSynonymSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CassetteSynonymSlotAnnotationDAO extends BaseSQLDAO<CassetteSynonymSlotAnnotation> {

	protected CassetteSynonymSlotAnnotationDAO() {
		super(CassetteSynonymSlotAnnotation.class);
	}
}
