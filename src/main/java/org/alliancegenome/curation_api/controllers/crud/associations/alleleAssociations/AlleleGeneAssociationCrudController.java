package org.alliancegenome.curation_api.controllers.crud.associations.alleleAssociations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleGeneAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.alleleAssociations.AlleleGeneAssociationCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleGeneAssociationService;

@RequestScoped
public class AlleleGeneAssociationCrudController extends
	BaseEntityCrudController<AlleleGeneAssociationService, AlleleGeneAssociation, AlleleGeneAssociationDAO> implements AlleleGeneAssociationCrudInterface {

	@Inject
	AlleleGeneAssociationService alleleGeneAssociationService;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleGeneAssociationService);
	}

	@Override
	public ObjectResponse<AlleleGeneAssociation> update(AlleleGeneAssociation entity) {
		return alleleGeneAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleGeneAssociation> create(AlleleGeneAssociation entity) {
		return alleleGeneAssociationService.upsert(entity);
	}

	public ObjectResponse<AlleleGeneAssociation> validate(AlleleGeneAssociation entity) {
		return alleleGeneAssociationService.validate(entity);
	}
}
