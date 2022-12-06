package org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;

@ApplicationScoped
public class GeneSymbolSlotAnnotationDAO extends BaseSQLDAO<GeneSymbolSlotAnnotation> {

	protected GeneSymbolSlotAnnotationDAO() {
		super(GeneSymbolSlotAnnotation.class);
	}

}
