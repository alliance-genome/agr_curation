package org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConstructSynonymSlotAnnotationDAO extends BaseSQLDAO<ConstructSynonymSlotAnnotation> {

	protected ConstructSynonymSlotAnnotationDAO() {
		super(ConstructSynonymSlotAnnotation.class);
	}
}
