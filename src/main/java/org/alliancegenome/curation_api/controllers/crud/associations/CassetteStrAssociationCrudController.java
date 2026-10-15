package org.alliancegenome.curation_api.controllers.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.CassetteStrAssociationDAO;
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

	@Inject CassetteStrAssociationService lCassetteStrAssociationService;
	@Inject CassetteStrAssociationExecutor lCassetteStrAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(lCassetteStrAssociationService);
	}

	@Override
	public ObjectResponse<CassetteStrAssociation> update(CassetteStrAssociation entity) {
		return lCassetteStrAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<CassetteStrAssociation> create(CassetteStrAssociation entity) {
		return lCassetteStrAssociationService.upsert(entity);
	}

	public ObjectResponse<CassetteStrAssociation> validate(CassetteStrAssociation entity) {
		return lCassetteStrAssociationService.validate(entity);
	}

	@Override
	public APIResponse updateCassetteStrAssociations(String dataProvider, List<CassetteStrAssociationDTO> associations) {
		return lCassetteStrAssociationExecutor.runLoadApi(lCassetteStrAssociationService, dataProvider, associations);
	}

	public ObjectResponse<CassetteStrAssociation> getAssociation(Long subjectId, String relationName, Long objectId) {
		return lCassetteStrAssociationService.getAssociation(subjectId, relationName, objectId);
	}
}
