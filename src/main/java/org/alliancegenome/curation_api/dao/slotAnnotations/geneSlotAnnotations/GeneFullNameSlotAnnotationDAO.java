package org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;

@ApplicationScoped
public class GeneFullNameSlotAnnotationDAO extends BaseSQLDAO<GeneFullNameSlotAnnotation> {

	protected GeneFullNameSlotAnnotationDAO() {
		super(GeneFullNameSlotAnnotation.class);
	}
	
}
