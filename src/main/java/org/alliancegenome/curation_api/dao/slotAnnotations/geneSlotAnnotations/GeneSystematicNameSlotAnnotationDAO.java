package org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;

@ApplicationScoped
public class GeneSystematicNameSlotAnnotationDAO extends BaseSQLDAO<GeneSystematicNameSlotAnnotation> {

	protected GeneSystematicNameSlotAnnotationDAO() {
		super(GeneSystematicNameSlotAnnotation.class);
	}
	
}
