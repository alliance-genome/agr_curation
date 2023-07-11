package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;

@ApplicationScoped
public class AlleleFunctionalImpactSlotAnnotationDAO extends BaseSQLDAO< AlleleFunctionalImpactSlotAnnotation> {

	protected AlleleFunctionalImpactSlotAnnotationDAO() {
		super( AlleleFunctionalImpactSlotAnnotation.class);
	}

}
