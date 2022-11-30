package org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;

@ApplicationScoped
public class GeneSynonymSlotAnnotationDAO extends BaseSQLDAO<GeneSynonymSlotAnnotation> {

	protected GeneSynonymSlotAnnotationDAO() {
		super(GeneSynonymSlotAnnotation.class);
	}
	
}
