package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructFullNameSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConstructFullNameSlotAnnotationDAO extends BaseSQLDAO<ConstructFullNameSlotAnnotation> {

	protected ConstructFullNameSlotAnnotationDAO() {
		super(ConstructFullNameSlotAnnotation.class);
	}
}
