package org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleFunctionalImpactSlotAnnotationService extends BaseEntityCrudService<AlleleFunctionalImpactSlotAnnotation, AlleleFunctionalImpactSlotAnnotationDAO> {

	@Inject
	AlleleFunctionalImpactSlotAnnotationDAO alleleFunctionalImpactDAO;
	@Inject
	AlleleFunctionalImpactSlotAnnotationValidator alleleFunctionalImpactValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleFunctionalImpactDAO);
	}

	@Transactional
	public ObjectResponse<AlleleFunctionalImpactSlotAnnotation> upsert(AlleleFunctionalImpactSlotAnnotation uiEntity) {
		AlleleFunctionalImpactSlotAnnotation dbEntity = alleleFunctionalImpactValidator.validateAlleleFunctionalImpactSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<AlleleFunctionalImpactSlotAnnotation>(alleleFunctionalImpactDAO.persist(dbEntity));
	}

	public ObjectResponse<AlleleFunctionalImpactSlotAnnotation> validate(AlleleFunctionalImpactSlotAnnotation uiEntity) {
		AlleleFunctionalImpactSlotAnnotation amt = alleleFunctionalImpactValidator.validateAlleleFunctionalImpactSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AlleleFunctionalImpactSlotAnnotation>(amt);
	}

}
