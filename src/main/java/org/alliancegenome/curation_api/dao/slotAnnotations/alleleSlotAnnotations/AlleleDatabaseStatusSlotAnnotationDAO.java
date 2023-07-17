package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotation;

@ApplicationScoped
public class AlleleDatabaseStatusSlotAnnotationDAO extends BaseSQLDAO<AlleleDatabaseStatusSlotAnnotation> {

	protected AlleleDatabaseStatusSlotAnnotationDAO() {
		super(AlleleDatabaseStatusSlotAnnotation.class);
	}

}
