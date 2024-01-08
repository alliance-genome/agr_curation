package org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConstructSymbolSlotAnnotationDAO extends BaseSQLDAO<ConstructSymbolSlotAnnotation> {

	protected ConstructSymbolSlotAnnotationDAO() {
		super(ConstructSymbolSlotAnnotation.class);
	}
}
