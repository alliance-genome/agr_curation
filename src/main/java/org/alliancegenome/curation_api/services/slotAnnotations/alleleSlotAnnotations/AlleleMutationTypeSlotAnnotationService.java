package org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleMutationTypeSlotAnnotationService extends BaseEntityCrudService<AlleleMutationTypeSlotAnnotation, AlleleMutationTypeSlotAnnotationDAO> {

	@Inject
	AlleleMutationTypeSlotAnnotationDAO alleleMutationTypeDAO;
	@Inject
	AlleleMutationTypeSlotAnnotationValidator alleleMutationTypeValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleMutationTypeDAO);
	}

	@Transactional
	public ObjectResponse<AlleleMutationTypeSlotAnnotation> upsert(AlleleMutationTypeSlotAnnotation uiEntity) {
		AlleleMutationTypeSlotAnnotation dbEntity = alleleMutationTypeValidator.validateAlleleMutationTypeSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<AlleleMutationTypeSlotAnnotation>(alleleMutationTypeDAO.persist(dbEntity));
	}

	public ObjectResponse<AlleleMutationTypeSlotAnnotation> validate(AlleleMutationTypeSlotAnnotation uiEntity) {
		AlleleMutationTypeSlotAnnotation amt = alleleMutationTypeValidator.validateAlleleMutationTypeSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AlleleMutationTypeSlotAnnotation>(amt);
	}

}
