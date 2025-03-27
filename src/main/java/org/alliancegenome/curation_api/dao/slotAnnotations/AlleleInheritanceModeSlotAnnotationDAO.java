package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleInheritanceModeSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleInheritanceModeSlotAnnotationDAO extends BaseSQLDAO<AlleleInheritanceModeSlotAnnotation> {

	protected AlleleInheritanceModeSlotAnnotationDAO() {
		super(AlleleInheritanceModeSlotAnnotation.class);
	}

}
