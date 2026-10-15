package org.alliancegenome.curation_api.controllers.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.CassetteTransgenicToolAssociationDAO;
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

	@Inject CassetteTransgenicToolAssociationService lCassetteTransgenicToolAssociationService;
	@Inject CassetteTransgenicToolAssociationExecutor lCassetteTransgenicToolAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(lCassetteTransgenicToolAssociationService);
	}

	@Override
	public ObjectResponse<CassetteTransgenicToolAssociation> update(CassetteTransgenicToolAssociation entity) {
		return lCassetteTransgenicToolAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<CassetteTransgenicToolAssociation> create(CassetteTransgenicToolAssociation entity) {
		return lCassetteTransgenicToolAssociationService.upsert(entity);
	}

	public ObjectResponse<CassetteTransgenicToolAssociation> validate(CassetteTransgenicToolAssociation entity) {
		return lCassetteTransgenicToolAssociationService.validate(entity);
	}

	@Override
	public APIResponse updateCassetteTransgenicToolAssociations(String dataProvider, List<CassetteTransgenicToolAssociationDTO> associations) {
		return lCassetteTransgenicToolAssociationExecutor.runLoadApi(lCassetteTransgenicToolAssociationService, dataProvider, associations);
	}

	public ObjectResponse<CassetteTransgenicToolAssociation> getAssociation(Long subjectId, String relationName, Long objectId) {
		return lCassetteTransgenicToolAssociationService.getAssociation(subjectId, relationName, objectId);
	}
}
