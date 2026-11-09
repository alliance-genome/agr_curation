package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSymbolSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

/** SCRUM-6535. */
@ApplicationScoped
public class CassetteSymbolSlotAnnotationDAO extends BaseSQLDAO<CassetteSymbolSlotAnnotation> {

	protected CassetteSymbolSlotAnnotationDAO() {
		super(CassetteSymbolSlotAnnotation.class);
	}
}
