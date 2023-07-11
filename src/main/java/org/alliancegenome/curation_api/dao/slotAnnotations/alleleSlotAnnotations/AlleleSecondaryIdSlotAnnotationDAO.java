package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleSecondaryIdSlotAnnotationDAO extends BaseSQLDAO<AlleleSecondaryIdSlotAnnotation> {

	protected AlleleSecondaryIdSlotAnnotationDAO() {
		super(AlleleSecondaryIdSlotAnnotation.class);
	}

}
