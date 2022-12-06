package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;

@ApplicationScoped
public class AlleleSynonymSlotAnnotationDAO extends BaseSQLDAO<AlleleSynonymSlotAnnotation> {

	protected AlleleSynonymSlotAnnotationDAO() {
		super(AlleleSynonymSlotAnnotation.class);
	}
	
}
