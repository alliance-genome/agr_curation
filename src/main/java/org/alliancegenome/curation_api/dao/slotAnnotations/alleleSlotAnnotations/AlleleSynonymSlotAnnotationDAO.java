package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleSynonymSlotAnnotationDAO extends BaseSQLDAO<AlleleSynonymSlotAnnotation> {

	protected AlleleSynonymSlotAnnotationDAO() {
		super(AlleleSynonymSlotAnnotation.class);
	}

}
