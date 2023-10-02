package org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;

@ApplicationScoped
public class ConstructSynonymSlotAnnotationDAO extends BaseSQLDAO<ConstructSynonymSlotAnnotation> {

	protected ConstructSynonymSlotAnnotationDAO() {
		super(ConstructSynonymSlotAnnotation.class);
	}
}
