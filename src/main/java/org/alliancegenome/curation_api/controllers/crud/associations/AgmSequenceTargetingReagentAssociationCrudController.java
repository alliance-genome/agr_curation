package org.alliancegenome.curation_api.controllers.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.AgmSequenceTargetingReagentAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.AgmSequenceTargetingReagentAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.AgmStrAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.AgmSequenceTargetingReagentAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AgmSequenceTargetingReagentAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.AgmStrAssociationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmSequenceTargetingReagentAssociationCrudController extends
	BaseEntityCrudController<AgmStrAssociationService, AgmSequenceTargetingReagentAssociation, AgmSequenceTargetingReagentAssociationDAO>
	implements AgmSequenceTargetingReagentAssociationCrudInterface {

	@Inject
	AgmStrAssociationService agmStrAssociationService;
	@Inject
	AgmStrAssociationExecutor agmStrAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(agmStrAssociationService);
	}

	public ObjectResponse<AgmSequenceTargetingReagentAssociation> getAssociation(Long agmId, String relationName, Long strId) {
		return agmStrAssociationService.getAssociation(agmId, relationName, strId);
	}

	@Override
	public APIResponse updateAgmStrAssociations(String dataProvider, List<AgmSequenceTargetingReagentAssociationDTO> associationData) {
		return agmStrAssociationExecutor.runLoadApi(agmStrAssociationService, dataProvider, associationData);

	}
}
