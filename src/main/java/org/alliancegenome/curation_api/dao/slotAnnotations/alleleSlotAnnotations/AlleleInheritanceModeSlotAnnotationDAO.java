package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleInheritanceModeSlotAnnotationDAO extends BaseSQLDAO<AlleleInheritanceModeSlotAnnotation> {

	protected AlleleInheritanceModeSlotAnnotationDAO() {
		super(AlleleInheritanceModeSlotAnnotation.class);
	}

}
