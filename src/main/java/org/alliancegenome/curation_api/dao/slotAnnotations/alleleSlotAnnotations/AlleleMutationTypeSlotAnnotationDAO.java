package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;

@ApplicationScoped
public class AlleleMutationTypeSlotAnnotationDAO extends BaseSQLDAO<AlleleMutationTypeSlotAnnotation> {

	protected AlleleMutationTypeSlotAnnotationDAO() {
		super(AlleleMutationTypeSlotAnnotation.class);
	}
	
}
