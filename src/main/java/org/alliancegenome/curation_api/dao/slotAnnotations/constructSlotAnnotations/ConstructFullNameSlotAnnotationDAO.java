package org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotation;

@ApplicationScoped
public class ConstructFullNameSlotAnnotationDAO extends BaseSQLDAO<ConstructFullNameSlotAnnotation> {

	protected ConstructFullNameSlotAnnotationDAO() {
		super(ConstructFullNameSlotAnnotation.class);
	}
}
