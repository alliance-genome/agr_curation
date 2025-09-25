package org.alliancegenome.curation_api.controllers.crud.associations;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.AlleleConstructAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.AlleleConstructAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.AlleleConstructAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AlleleConstructAssociationDTO;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.associations.AlleleConstructAssociationService;
import org.alliancegenome.curation_api.view.View;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleConstructAssociationCrudController extends
		BaseEntityCrudController<AlleleConstructAssociationService, AlleleConstructAssociation, AlleleConstructAssociationDAO> implements AlleleConstructAssociationCrudInterface {

	@Inject
	AlleleConstructAssociationService alleleConstructAssociationService;
	@Inject
	AlleleConstructAssociationExecutor alleleConstructAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(alleleConstructAssociationService);
	}

	@Override
	public ObjectResponse<AlleleConstructAssociation> update(AlleleConstructAssociation entity) {
		return alleleConstructAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<AlleleConstructAssociation> create(AlleleConstructAssociation entity) {
		return alleleConstructAssociationService.upsert(entity);
	}

	public ObjectResponse<AlleleConstructAssociation> validate(AlleleConstructAssociation entity) {
		return alleleConstructAssociationService.validate(entity);
	}

	public APIResponse updateAlleleConstructAssociations(String dataProvider,
														List<AlleleConstructAssociationDTO> associations) {
		return alleleConstructAssociationExecutor.runLoadApi(alleleConstructAssociationService, dataProvider, associations);
	}

	public SearchResponse<AlleleConstructAssociation> findForPublic(Integer page, Integer limit, HashMap<String, Object> params) {
		Pagination pagination = new Pagination(page, limit);
		return service.findByParams(pagination, params);
	}

	public ObjectResponse<AlleleConstructAssociation> getAssociation(Long alleleId, String relationName,
																	 Long constructId) {
		return alleleConstructAssociationService.getAssociation(alleleId, relationName, constructId);
	}
}
