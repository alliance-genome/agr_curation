package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;

@ApplicationScoped
public class AlleleGermlineTransmissionStatusSlotAnnotationDAO extends BaseSQLDAO<AlleleGermlineTransmissionStatusSlotAnnotation> {

	protected AlleleGermlineTransmissionStatusSlotAnnotationDAO() {
		super(AlleleGermlineTransmissionStatusSlotAnnotation.class);
	}

}