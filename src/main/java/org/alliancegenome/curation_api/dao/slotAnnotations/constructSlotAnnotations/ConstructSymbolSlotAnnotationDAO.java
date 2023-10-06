package org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;

@ApplicationScoped
public class ConstructSymbolSlotAnnotationDAO extends BaseSQLDAO<ConstructSymbolSlotAnnotation> {

	protected ConstructSymbolSlotAnnotationDAO() {
		super(ConstructSymbolSlotAnnotation.class);
	}
}
