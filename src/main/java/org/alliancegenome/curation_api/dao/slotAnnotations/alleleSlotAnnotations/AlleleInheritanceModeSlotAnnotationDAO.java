package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;

@ApplicationScoped
public class AlleleInheritanceModeSlotAnnotationDAO extends BaseSQLDAO<AlleleInheritanceModeSlotAnnotation> {

	protected AlleleInheritanceModeSlotAnnotationDAO() {
		super(AlleleInheritanceModeSlotAnnotation.class);
	}

}
