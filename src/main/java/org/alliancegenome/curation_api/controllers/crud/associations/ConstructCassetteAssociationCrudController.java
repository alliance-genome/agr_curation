package org.alliancegenome.curation_api.controllers.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.ConstructCassetteAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.ConstructCassetteAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.ConstructCassetteAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.ConstructCassetteAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.ConstructCassetteAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.ConstructCassetteAssociationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/** SCRUM-6535. Mirrors ConstructGenomicEntityAssociationCrudController. */
@RequestScoped
public class ConstructCassetteAssociationCrudController extends
	BaseEntityCrudController<ConstructCassetteAssociationService, ConstructCassetteAssociation, ConstructCassetteAssociationDAO> implements ConstructCassetteAssociationCrudInterface {

	@Inject ConstructCassetteAssociationService lConstructCassetteAssociationService;
	@Inject ConstructCassetteAssociationExecutor lConstructCassetteAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(lConstructCassetteAssociationService);
	}

	@Override
	public ObjectResponse<ConstructCassetteAssociation> update(ConstructCassetteAssociation entity) {
		return lConstructCassetteAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<ConstructCassetteAssociation> create(ConstructCassetteAssociation entity) {
		return lConstructCassetteAssociationService.upsert(entity);
	}

	public ObjectResponse<ConstructCassetteAssociation> validate(ConstructCassetteAssociation entity) {
		return lConstructCassetteAssociationService.validate(entity);
	}

	@Override
	public APIResponse updateConstructCassetteAssociations(String dataProvider, List<ConstructCassetteAssociationDTO> associations) {
		return lConstructCassetteAssociationExecutor.runLoadApi(lConstructCassetteAssociationService, dataProvider, associations);
	}

	public ObjectResponse<ConstructCassetteAssociation> getAssociation(Long subjectId, String relationName, Long objectId) {
		return lConstructCassetteAssociationService.getAssociation(subjectId, relationName, objectId);
	}
}
