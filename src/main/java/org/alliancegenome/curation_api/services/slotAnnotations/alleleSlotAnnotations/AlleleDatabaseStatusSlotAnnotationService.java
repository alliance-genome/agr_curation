package org.alliancegenome.curation_api.services.slotAnnotations.alleleSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotationValidator;

@RequestScoped
public class AlleleDatabaseStatusSlotAnnotationService extends BaseEntityCrudService<AlleleDatabaseStatusSlotAnnotation, AlleleDatabaseStatusSlotAnnotationDAO> {

	@Inject
	AlleleDatabaseStatusSlotAnnotationDAO alleleDatabaseStatusDAO;
	@Inject
	AlleleDatabaseStatusSlotAnnotationValidator alleleDatabaseStatusValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDatabaseStatusDAO);
	}

	@Transactional
	public ObjectResponse<AlleleDatabaseStatusSlotAnnotation> upsert(AlleleDatabaseStatusSlotAnnotation uiEntity) {
		AlleleDatabaseStatusSlotAnnotation dbEntity = alleleDatabaseStatusValidator.validateAlleleDatabaseStatusSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<AlleleDatabaseStatusSlotAnnotation>(alleleDatabaseStatusDAO.persist(dbEntity));
	}

	public ObjectResponse<AlleleDatabaseStatusSlotAnnotation> validate(AlleleDatabaseStatusSlotAnnotation uiEntity) {
		AlleleDatabaseStatusSlotAnnotation ads = alleleDatabaseStatusValidator.validateAlleleDatabaseStatusSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<AlleleDatabaseStatusSlotAnnotation>(ads);
	}

}
