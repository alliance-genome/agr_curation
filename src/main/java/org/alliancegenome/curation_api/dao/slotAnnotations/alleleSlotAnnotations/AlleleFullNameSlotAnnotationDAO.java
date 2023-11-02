package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleFullNameSlotAnnotationDAO extends BaseSQLDAO<AlleleFullNameSlotAnnotation> {

	protected AlleleFullNameSlotAnnotationDAO() {
		super(AlleleFullNameSlotAnnotation.class);
	}

}
