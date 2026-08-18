package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSynonymSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransgenicToolSynonymSlotAnnotationDAO extends BaseSQLDAO<TransgenicToolSynonymSlotAnnotation> {

	protected TransgenicToolSynonymSlotAnnotationDAO() {
		super(TransgenicToolSynonymSlotAnnotation.class);
	}
}
