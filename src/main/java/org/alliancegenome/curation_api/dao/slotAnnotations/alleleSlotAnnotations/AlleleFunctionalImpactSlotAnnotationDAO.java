package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleFunctionalImpactSlotAnnotationDAO extends BaseSQLDAO< AlleleFunctionalImpactSlotAnnotation> {

	protected AlleleFunctionalImpactSlotAnnotationDAO() {
		super( AlleleFunctionalImpactSlotAnnotation.class);
	}

}
