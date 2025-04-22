package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleGermlineTransmissionStatusSlotAnnotationDAO extends BaseSQLDAO<AlleleGermlineTransmissionStatusSlotAnnotation> {

	protected AlleleGermlineTransmissionStatusSlotAnnotationDAO() {
		super(AlleleGermlineTransmissionStatusSlotAnnotation.class);
	}

}