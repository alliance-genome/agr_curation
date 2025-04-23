package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSymbolSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneSymbolSlotAnnotationDAO extends BaseSQLDAO<GeneSymbolSlotAnnotation> {

	protected GeneSymbolSlotAnnotationDAO() {
		super(GeneSymbolSlotAnnotation.class);
	}

}
