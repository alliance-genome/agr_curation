package org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotation;

@ApplicationScoped
public class AlleleNomenclatureEventSlotAnnotationDAO extends BaseSQLDAO<AlleleNomenclatureEventSlotAnnotation> {

	protected AlleleNomenclatureEventSlotAnnotationDAO() {
		super(AlleleNomenclatureEventSlotAnnotation.class);
	}

}
