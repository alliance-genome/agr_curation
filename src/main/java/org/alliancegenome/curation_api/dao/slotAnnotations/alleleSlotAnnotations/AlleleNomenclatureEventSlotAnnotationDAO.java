package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleNomenclatureEventSlotAnnotationDAO extends BaseSQLDAO<AlleleNomenclatureEventSlotAnnotation> {

	protected AlleleNomenclatureEventSlotAnnotationDAO() {
		super(AlleleNomenclatureEventSlotAnnotation.class);
	}

}
