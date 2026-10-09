package org.alliancegenome.curation_api.controllers.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.CassetteTransgenicToolAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.interfaces.crud.associations.CassetteTransgenicToolAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.CassetteTransgenicToolAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.CassetteTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteTransgenicToolAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.CassetteTransgenicToolAssociationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/** SCRUM-6535. Mirrors ConstructGenomicEntityAssociationCrudController. */
@RequestScoped
public class CassetteTransgenicToolAssociationCrudController extends
	BaseEntityCrudController<CassetteTransgenicToolAssociationService, CassetteTransgenicToolAssociation, CassetteTransgenicToolAssociationDAO> implements CassetteTransgenicToolAssociationCrudInterface {

	@Inject CassetteTransgenicToolAssociationService cassetteTransgenicToolAssociationService;
	@Inject CassetteTransgenicToolAssociationExecutor cassetteTransgenicToolAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(cassetteTransgenicToolAssociationService);
	}

	@Override
	public ObjectResponse<CassetteTransgenicToolAssociation> update(CassetteTransgenicToolAssociation entity) {
		return cassetteTransgenicToolAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<CassetteTransgenicToolAssociation> create(CassetteTransgenicToolAssociation entity) {
		return cassetteTransgenicToolAssociationService.upsert(entity);
	}

	public ObjectResponse<CassetteTransgenicToolAssociation> validate(CassetteTransgenicToolAssociation entity) {
		return cassetteTransgenicToolAssociationService.validate(entity);
	}

	@Override
	public APIResponse updateCassetteTransgenicToolAssociations(String dataProvider, List<CassetteTransgenicToolAssociationDTO> associations) {
		APIResponse response = cassetteTransgenicToolAssociationExecutor.runLoadApi(cassetteTransgenicToolAssociationService, dataProvider, associations);
		cassetteTransgenicToolAssociationExecutor.reindexSubjects(BackendBulkDataProvider.valueOf(dataProvider));
		return response;
	}

	public ObjectResponse<CassetteTransgenicToolAssociation> getAssociation(Long subjectId, String relationName, Long objectId) {
		return cassetteTransgenicToolAssociationService.getAssociation(subjectId, relationName, objectId);
	}
}
