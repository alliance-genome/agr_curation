package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolUseSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransgenicToolUseSlotAnnotationDAO extends BaseSQLDAO<TransgenicToolUseSlotAnnotation> {

	protected TransgenicToolUseSlotAnnotationDAO() {
		super(TransgenicToolUseSlotAnnotation.class);
	}
}
