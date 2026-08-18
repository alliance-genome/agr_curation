package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolFullNameSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransgenicToolFullNameSlotAnnotationDAO extends BaseSQLDAO<TransgenicToolFullNameSlotAnnotation> {

	protected TransgenicToolFullNameSlotAnnotationDAO() {
		super(TransgenicToolFullNameSlotAnnotation.class);
	}
}
