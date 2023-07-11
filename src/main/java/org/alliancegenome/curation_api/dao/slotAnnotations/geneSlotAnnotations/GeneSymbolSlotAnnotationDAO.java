package org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneSymbolSlotAnnotationDAO extends BaseSQLDAO<GeneSymbolSlotAnnotation> {

	protected GeneSymbolSlotAnnotationDAO() {
		super(GeneSymbolSlotAnnotation.class);
	}

}
