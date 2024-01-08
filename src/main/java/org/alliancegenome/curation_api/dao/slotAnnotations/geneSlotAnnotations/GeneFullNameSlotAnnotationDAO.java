package org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneFullNameSlotAnnotationDAO extends BaseSQLDAO<GeneFullNameSlotAnnotation> {

	protected GeneFullNameSlotAnnotationDAO() {
		super(GeneFullNameSlotAnnotation.class);
	}

}
