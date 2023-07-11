package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleSymbolSlotAnnotationDAO extends BaseSQLDAO<AlleleSymbolSlotAnnotation> {

	protected AlleleSymbolSlotAnnotationDAO() {
		super(AlleleSymbolSlotAnnotation.class);
	}

}
