package org.alliancegenome.curation_api.controllers.crud.associations;

import java.util.Map;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.AlleleVariantAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.AlleleVariantAssociationCrudInterface;
import org.alliancegenome.curation_api.model.entities.associations.AlleleVariantAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.AlleleVariantAssociationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleVariantAssociationCrudController extends
		BaseEntityCrudController<AlleleVariantAssociationService, AlleleVariantAssociation, AlleleVariantAssociationDAO> implements AlleleVariantAssociationCrudInterface {

	@Inject
	AlleleVariantAssociationService alleleVariantAssociationService;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleVariantAssociationService);
	}

	@Override
	public ObjectResponse<AlleleVariantAssociation> update(AlleleVariantAssociation entity) {
		return alleleVariantAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleVariantAssociation> create(AlleleVariantAssociation entity) {
		return alleleVariantAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleVariantAssociation> getAssociation(Long alleleId, String relationName, Long variantId) {
		return alleleVariantAssociationService.getAssociation(alleleId, relationName, variantId);
	}

	@Override
	public ObjectResponse<Map<String, Long>> alleleVariantAssociationMap() {
		Map<String, Long> map = alleleVariantAssociationService.getAlleleVariantAssociationMap();
		ObjectResponse<Map<String, Long>> response = new ObjectResponse<>();
		response.setEntity(map);
		return response;
	}
}
