package org.alliancegenome.curation_api.controllers.crud.associations;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.AgmAgmAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.AgmAgmAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.AgmAgmAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.AgmAgmAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AgmAgmAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.AgmAgmAssociationService;

import java.util.List;

@RequestScoped
public class AgmAgmAssociationCrudController extends
	BaseEntityCrudController<AgmAgmAssociationService, AgmAgmAssociation, AgmAgmAssociationDAO>
	implements AgmAgmAssociationCrudInterface {

	@Inject
	AgmAgmAssociationService agmAgmAssociationService;
	@Inject
	AgmAgmAssociationExecutor agmAgmAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(agmAgmAssociationService);
	}

	public ObjectResponse<AgmAgmAssociation> getAssociation(Long alleleId, String relationName, Long geneId) {
		return agmAgmAssociationService.getAssociation(alleleId, relationName, geneId);
	}

	@Override
	public APIResponse updateAgmAgmAssociations(String dataProvider, List<AgmAgmAssociationDTO> associationData) {
		return agmAgmAssociationExecutor.runLoadApi(agmAgmAssociationService, dataProvider, associationData);

	}
}
