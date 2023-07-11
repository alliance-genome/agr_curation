package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleMutationTypeSlotAnnotationDAO extends BaseSQLDAO<AlleleMutationTypeSlotAnnotation> {

	protected AlleleMutationTypeSlotAnnotationDAO() {
		super(AlleleMutationTypeSlotAnnotation.class);
	}

}
