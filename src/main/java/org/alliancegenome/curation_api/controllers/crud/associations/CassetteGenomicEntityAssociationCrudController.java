package org.alliancegenome.curation_api.controllers.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.CassetteGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.CassetteGenomicEntityAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.CassetteGenomicEntityAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.CassetteGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.CassetteGenomicEntityAssociationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/** SCRUM-6535. Mirrors ConstructGenomicEntityAssociationCrudController. */
@RequestScoped
public class CassetteGenomicEntityAssociationCrudController extends
	BaseEntityCrudController<CassetteGenomicEntityAssociationService, CassetteGenomicEntityAssociation, CassetteGenomicEntityAssociationDAO> implements CassetteGenomicEntityAssociationCrudInterface {

	@Inject CassetteGenomicEntityAssociationService lCassetteGenomicEntityAssociationService;
	@Inject CassetteGenomicEntityAssociationExecutor lCassetteGenomicEntityAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(lCassetteGenomicEntityAssociationService);
	}

	@Override
	public ObjectResponse<CassetteGenomicEntityAssociation> update(CassetteGenomicEntityAssociation entity) {
		return lCassetteGenomicEntityAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<CassetteGenomicEntityAssociation> create(CassetteGenomicEntityAssociation entity) {
		return lCassetteGenomicEntityAssociationService.upsert(entity);
	}

	public ObjectResponse<CassetteGenomicEntityAssociation> validate(CassetteGenomicEntityAssociation entity) {
		return lCassetteGenomicEntityAssociationService.validate(entity);
	}

	@Override
	public APIResponse updateCassetteGenomicEntityAssociations(String dataProvider, List<CassetteGenomicEntityAssociationDTO> associations) {
		return lCassetteGenomicEntityAssociationExecutor.runLoadApi(lCassetteGenomicEntityAssociationService, dataProvider, associations);
	}

	public ObjectResponse<CassetteGenomicEntityAssociation> getAssociation(Long subjectId, String relationName, Long objectId) {
		return lCassetteGenomicEntityAssociationService.getAssociation(subjectId, relationName, objectId);
	}
}
