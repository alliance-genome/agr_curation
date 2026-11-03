package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteUseSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

/** SCRUM-6535. */
@ApplicationScoped
public class CassetteUseSlotAnnotationDAO extends BaseSQLDAO<CassetteUseSlotAnnotation> {

	protected CassetteUseSlotAnnotationDAO() {
		super(CassetteUseSlotAnnotation.class);
	}
}
