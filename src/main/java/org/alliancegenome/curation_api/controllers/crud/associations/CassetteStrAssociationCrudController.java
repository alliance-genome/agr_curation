package org.alliancegenome.curation_api.controllers.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.CassetteStrAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.interfaces.crud.associations.CassetteStrAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.CassetteStrAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.CassetteStrAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteStrAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.CassetteStrAssociationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/** SCRUM-6535. Mirrors ConstructGenomicEntityAssociationCrudController. */
@RequestScoped
public class CassetteStrAssociationCrudController extends
	BaseEntityCrudController<CassetteStrAssociationService, CassetteStrAssociation, CassetteStrAssociationDAO> implements CassetteStrAssociationCrudInterface {

	@Inject CassetteStrAssociationService cassetteStrAssociationService;
	@Inject CassetteStrAssociationExecutor cassetteStrAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(cassetteStrAssociationService);
	}

	@Override
	public ObjectResponse<CassetteStrAssociation> update(CassetteStrAssociation entity) {
		return cassetteStrAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<CassetteStrAssociation> create(CassetteStrAssociation entity) {
		return cassetteStrAssociationService.upsert(entity);
	}

	public ObjectResponse<CassetteStrAssociation> validate(CassetteStrAssociation entity) {
		return cassetteStrAssociationService.validate(entity);
	}

	@Override
	public APIResponse updateCassetteStrAssociations(String dataProvider, List<CassetteStrAssociationDTO> associations) {
		APIResponse response = cassetteStrAssociationExecutor.runLoadApi(cassetteStrAssociationService, dataProvider, associations);
		cassetteStrAssociationExecutor.reindexSubjects(BackendBulkDataProvider.valueOf(dataProvider));
		return response;
	}

	public ObjectResponse<CassetteStrAssociation> getAssociation(Long subjectId, String relationName, Long objectId) {
		return cassetteStrAssociationService.getAssociation(subjectId, relationName, objectId);
	}
}
