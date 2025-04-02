package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleFunctionalImpactSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleFunctionalImpactSlotAnnotationDAO extends BaseSQLDAO<AlleleFunctionalImpactSlotAnnotation> {

	protected AlleleFunctionalImpactSlotAnnotationDAO() {
		super(AlleleFunctionalImpactSlotAnnotation.class);
	}

}
