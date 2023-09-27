package org.alliancegenome.curation_api.services.associations.alleleAssociations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleGeneAssociationDAO;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.associations.alleleAssociations.AlleleGeneAssociationValidator;

@RequestScoped
public class AlleleGeneAssociationService extends BaseEntityCrudService<AlleleGeneAssociation, AlleleGeneAssociationDAO> {

	@Inject
	AlleleGeneAssociationDAO alleleGeneAssociationDAO;
	@Inject
	AlleleGeneAssociationValidator alleleGeneAssociationValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleGeneAssociationDAO);
	}

	@Transactional
	public ObjectResponse<AlleleGeneAssociation> upsert(AlleleGeneAssociation uiEntity) {
		AlleleGeneAssociation dbEntity = alleleGeneAssociationValidator.validateAlleleGeneAssociation(uiEntity, true, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<AlleleGeneAssociation>(alleleGeneAssociationDAO.persist(dbEntity));
	}

	public ObjectResponse<AlleleGeneAssociation> validate(AlleleGeneAssociation uiEntity) {
		AlleleGeneAssociation aga = alleleGeneAssociationValidator.validateAlleleGeneAssociation(uiEntity, true, false);
		return new ObjectResponse<AlleleGeneAssociation>(aga);
	}

}
