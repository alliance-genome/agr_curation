package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;

@ApplicationScoped
public class AlleleSecondaryIdSlotAnnotationDAO extends BaseSQLDAO<AlleleSecondaryIdSlotAnnotation> {

	protected AlleleSecondaryIdSlotAnnotationDAO() {
		super(AlleleSecondaryIdSlotAnnotation.class);
	}

}
