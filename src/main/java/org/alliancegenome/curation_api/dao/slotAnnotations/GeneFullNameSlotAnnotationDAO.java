package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneFullNameSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneFullNameSlotAnnotationDAO extends BaseSQLDAO<GeneFullNameSlotAnnotation> {

	protected GeneFullNameSlotAnnotationDAO() {
		super(GeneFullNameSlotAnnotation.class);
	}

}
