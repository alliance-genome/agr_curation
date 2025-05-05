package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSynonymSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgmSynonymSlotAnnotationDAO extends BaseSQLDAO<AgmSynonymSlotAnnotation> {

	protected AgmSynonymSlotAnnotationDAO() {
		super(AgmSynonymSlotAnnotation.class);
	}

}
