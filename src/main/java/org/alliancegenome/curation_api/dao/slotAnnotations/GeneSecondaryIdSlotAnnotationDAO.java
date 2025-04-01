package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSecondaryIdSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneSecondaryIdSlotAnnotationDAO extends BaseSQLDAO<GeneSecondaryIdSlotAnnotation> {

	protected GeneSecondaryIdSlotAnnotationDAO() {
		super(GeneSecondaryIdSlotAnnotation.class);
	}

}
