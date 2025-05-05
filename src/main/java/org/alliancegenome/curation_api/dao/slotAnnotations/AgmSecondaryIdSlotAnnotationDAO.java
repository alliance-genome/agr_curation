package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSecondaryIdSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgmSecondaryIdSlotAnnotationDAO extends BaseSQLDAO<AgmSecondaryIdSlotAnnotation> {

	protected AgmSecondaryIdSlotAnnotationDAO() {
		super(AgmSecondaryIdSlotAnnotation.class);
	}

}
