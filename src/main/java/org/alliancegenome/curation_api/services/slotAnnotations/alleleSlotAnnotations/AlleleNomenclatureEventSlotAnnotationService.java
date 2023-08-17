package org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationValidator;

@RequestScoped
public class AlleleNomenclatureEventSlotAnnotationService extends BaseEntityCrudService<AlleleNomenclatureEventSlotAnnotation, AlleleNomenclatureEventSlotAnnotationDAO> {

	@Inject
	AlleleNomenclatureEventSlotAnnotationDAO alleleNomenclatureEventDAO;
	@Inject
	AlleleNomenclatureEventSlotAnnotationValidator alleleNomenclatureEventValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleNomenclatureEventDAO);
	}

	@Transactional
	public ObjectResponse<AlleleNomenclatureEventSlotAnnotation> upsert(AlleleNomenclatureEventSlotAnnotation uiEntity) {
		AlleleNomenclatureEventSlotAnnotation dbEntity = alleleNomenclatureEventValidator.validateAlleleNomenclatureEventSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<AlleleNomenclatureEventSlotAnnotation>(alleleNomenclatureEventDAO.persist(dbEntity));
	}

	public ObjectResponse<AlleleNomenclatureEventSlotAnnotation> validate(AlleleNomenclatureEventSlotAnnotation uiEntity) {
		AlleleNomenclatureEventSlotAnnotation amt = alleleNomenclatureEventValidator.validateAlleleNomenclatureEventSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AlleleNomenclatureEventSlotAnnotation>(amt);
	}

}