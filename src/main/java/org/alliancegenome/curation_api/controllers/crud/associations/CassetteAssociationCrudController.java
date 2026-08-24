package org.alliancegenome.curation_api.controllers.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.CassetteAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.CassetteAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.CassetteAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.CassetteAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.CassetteAssociationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CassetteAssociationCrudController extends
	BaseEntityCrudController<CassetteAssociationService, CassetteAssociation, CassetteAssociationDAO> implements CassetteAssociationCrudInterface {

	@Inject
	CassetteAssociationService cassetteAssociationService;
	@Inject
	CassetteAssociationExecutor cassetteAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(cassetteAssociationService);
	}

	@Override
	public ObjectResponse<CassetteAssociation> update(CassetteAssociation entity) {
		return cassetteAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<CassetteAssociation> create(CassetteAssociation entity) {
		return cassetteAssociationService.upsert(entity);
	}

	public ObjectResponse<CassetteAssociation> validate(CassetteAssociation entity) {
		return cassetteAssociationService.validate(entity);
	}

	@Override
	public APIResponse updateCassetteAssociations(String dataProvider,
			List<CassetteAssociationDTO> associations) {
		return cassetteAssociationExecutor.runLoadApi(cassetteAssociationService, dataProvider, associations);
	}

	public ObjectResponse<CassetteAssociation> getAssociation(Long cassetteId, String relationName,
			Long genomicEntityId) {
		return cassetteAssociationService.getAssociation(cassetteId, relationName, genomicEntityId);
	}
}
