package org.alliancegenome.curation_api.controllers.crud.associations.constructAssociations;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.constructAssociations.ConstructGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.constructAssociations.ConstructGenomicEntityAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.constructAssociations.ConstructGenomicEntityAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.constructAssociations.ConstructGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.constructAssociations.ConstructGenomicEntityAssociationService;

@RequestScoped
public class ConstructGenomicEntityAssociationCrudController extends
	BaseEntityCrudController<ConstructGenomicEntityAssociationService, ConstructGenomicEntityAssociation, ConstructGenomicEntityAssociationDAO> implements ConstructGenomicEntityAssociationCrudInterface {

	@Inject
	ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;
	@Inject
	ConstructGenomicEntityAssociationExecutor constructGenomicEntityAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(constructGenomicEntityAssociationService);
	}

	@Override
	public ObjectResponse<ConstructGenomicEntityAssociation> update(ConstructGenomicEntityAssociation entity) {
		return constructGenomicEntityAssociationService.upsert(entity);
	}

	@Override
	public ObjectResponse<ConstructGenomicEntityAssociation> create(ConstructGenomicEntityAssociation entity) {
		return constructGenomicEntityAssociationService.upsert(entity);
	}

	public ObjectResponse<ConstructGenomicEntityAssociation> validate(ConstructGenomicEntityAssociation entity) {
		return constructGenomicEntityAssociationService.validate(entity);
	}

	@Override
	public APIResponse updateConstructGenomicEntityAssociations(String dataProvider,
			List<ConstructGenomicEntityAssociationDTO> associations) {
		return constructGenomicEntityAssociationExecutor.runLoad(dataProvider, associations);
	}

	public ObjectResponse<ConstructGenomicEntityAssociation> getAssociation(Long constructId, String relationName,
			String genomicEntityCurie) {
		return constructGenomicEntityAssociationService.getAssociation(constructId, relationName, genomicEntityCurie);
	}
}
