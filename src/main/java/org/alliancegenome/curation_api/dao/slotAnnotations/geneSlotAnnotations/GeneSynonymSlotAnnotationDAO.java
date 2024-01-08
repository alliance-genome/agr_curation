package org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneSynonymSlotAnnotationDAO extends BaseSQLDAO<GeneSynonymSlotAnnotation> {

	protected GeneSynonymSlotAnnotationDAO() {
		super(GeneSynonymSlotAnnotation.class);
	}

}
