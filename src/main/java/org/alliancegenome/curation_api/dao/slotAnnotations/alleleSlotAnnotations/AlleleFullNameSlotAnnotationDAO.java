package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;

@ApplicationScoped
public class AlleleFullNameSlotAnnotationDAO extends BaseSQLDAO<AlleleFullNameSlotAnnotation> {

	protected AlleleFullNameSlotAnnotationDAO() {
		super(AlleleFullNameSlotAnnotation.class);
	}

}
