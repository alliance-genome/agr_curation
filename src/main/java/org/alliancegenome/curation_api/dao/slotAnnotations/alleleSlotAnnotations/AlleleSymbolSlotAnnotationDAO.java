package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;

@ApplicationScoped
public class AlleleSymbolSlotAnnotationDAO extends BaseSQLDAO<AlleleSymbolSlotAnnotation> {

	protected AlleleSymbolSlotAnnotationDAO() {
		super(AlleleSymbolSlotAnnotation.class);
	}

}
