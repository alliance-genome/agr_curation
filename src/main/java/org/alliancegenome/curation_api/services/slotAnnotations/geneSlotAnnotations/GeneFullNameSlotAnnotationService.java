package org.alliancegenome.curation_api.services.slotAnnotations.geneSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotationValidator;

@RequestScoped
public class GeneFullNameSlotAnnotationService extends BaseEntityCrudService<GeneFullNameSlotAnnotation, GeneFullNameSlotAnnotationDAO> {

	@Inject GeneFullNameSlotAnnotationDAO geneFullNameDAO;
	@Inject GeneFullNameSlotAnnotationValidator geneFullNameValidator;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneFullNameDAO);
	}
	
	@Transactional
	public ObjectResponse<GeneFullNameSlotAnnotation> upsert(GeneFullNameSlotAnnotation uiEntity) {
		GeneFullNameSlotAnnotation dbEntity = geneFullNameValidator.validateGeneFullNameSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<GeneFullNameSlotAnnotation>(geneFullNameDAO.persist(dbEntity));
	}
	
	public ObjectResponse<GeneFullNameSlotAnnotation> validate(GeneFullNameSlotAnnotation uiEntity) {
		GeneFullNameSlotAnnotation fullName = geneFullNameValidator.validateGeneFullNameSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<GeneFullNameSlotAnnotation>(fullName);
	}
	
}
