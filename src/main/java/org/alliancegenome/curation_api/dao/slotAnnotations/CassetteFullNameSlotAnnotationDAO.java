package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteFullNameSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CassetteFullNameSlotAnnotationDAO extends BaseSQLDAO<CassetteFullNameSlotAnnotation> {

	protected CassetteFullNameSlotAnnotationDAO() {
		super(CassetteFullNameSlotAnnotation.class);
	}
}
