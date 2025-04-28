package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmFullNameSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgmFullNameSlotAnnotationDAO extends BaseSQLDAO<AgmFullNameSlotAnnotation> {

	protected AgmFullNameSlotAnnotationDAO() {
		super(AgmFullNameSlotAnnotation.class);
	}

}
