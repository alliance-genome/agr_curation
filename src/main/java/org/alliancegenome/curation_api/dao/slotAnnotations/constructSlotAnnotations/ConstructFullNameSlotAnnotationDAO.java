package org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConstructFullNameSlotAnnotationDAO extends BaseSQLDAO<ConstructFullNameSlotAnnotation> {

	protected ConstructFullNameSlotAnnotationDAO() {
		super(ConstructFullNameSlotAnnotation.class);
	}
}
