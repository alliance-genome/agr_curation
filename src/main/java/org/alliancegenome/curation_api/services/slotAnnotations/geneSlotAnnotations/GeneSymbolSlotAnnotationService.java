package org.alliancegenome.curation_api.services.slotAnnotations.geneSlotAnnotations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotationValidator;

@RequestScoped
public class GeneSymbolSlotAnnotationService extends BaseEntityCrudService<GeneSymbolSlotAnnotation, GeneSymbolSlotAnnotationDAO> {

	@Inject GeneSymbolSlotAnnotationDAO geneSymbolDAO;
	@Inject GeneSymbolSlotAnnotationValidator geneSymbolValidator;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneSymbolDAO);
	}
	
	@Transactional
	public ObjectResponse<GeneSymbolSlotAnnotation> upsert(GeneSymbolSlotAnnotation uiEntity) {
		GeneSymbolSlotAnnotation dbEntity = geneSymbolValidator.validateGeneSymbolSlotAnnotation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<GeneSymbolSlotAnnotation>(geneSymbolDAO.persist(dbEntity));
	}
	
	public ObjectResponse<GeneSymbolSlotAnnotation> validate(GeneSymbolSlotAnnotation uiEntity) {
		GeneSymbolSlotAnnotation symbol = geneSymbolValidator.validateGeneSymbolSlotAnnotation(uiEntity, true, false);
		return new ObjectResponse<GeneSymbolSlotAnnotation>(symbol);
	}
	
}
