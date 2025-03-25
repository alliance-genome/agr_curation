package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructComponentSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConstructComponentSlotAnnotationDAO extends BaseSQLDAO<ConstructComponentSlotAnnotation> {

	protected ConstructComponentSlotAnnotationDAO() {
		super(ConstructComponentSlotAnnotation.class);
	}
}
