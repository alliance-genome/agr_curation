package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleDatabaseStatusSlotAnnotationDAO extends BaseSQLDAO<AlleleDatabaseStatusSlotAnnotation> {

	protected AlleleDatabaseStatusSlotAnnotationDAO() {
		super(AlleleDatabaseStatusSlotAnnotation.class);
	}

}
