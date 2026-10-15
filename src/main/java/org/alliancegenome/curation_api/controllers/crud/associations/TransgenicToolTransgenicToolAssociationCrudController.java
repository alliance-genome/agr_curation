package org.alliancegenome.curation_api.controllers.crud.associations;

import java.util.List;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.associations.TransgenicToolTransgenicToolAssociationDAO;
import org.alliancegenome.curation_api.interfaces.crud.associations.TransgenicToolTransgenicToolAssociationCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.associations.TransgenicToolTransgenicToolAssociationExecutor;
import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.TransgenicToolTransgenicToolAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.associations.TransgenicToolTransgenicToolAssociationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/** SCRUM-6543. Mirrors AgmAgmAssociationCrudController. */
@RequestScoped
public class TransgenicToolTransgenicToolAssociationCrudController extends
	BaseEntityCrudController<TransgenicToolTransgenicToolAssociationService, TransgenicToolTransgenicToolAssociation, TransgenicToolTransgenicToolAssociationDAO>
	implements TransgenicToolTransgenicToolAssociationCrudInterface {

	@Inject TransgenicToolTransgenicToolAssociationService transgenicToolTransgenicToolAssociationService;
	@Inject TransgenicToolTransgenicToolAssociationExecutor transgenicToolTransgenicToolAssociationExecutor;

	@Override
	@PostConstruct
	protected void init() {
		setService(transgenicToolTransgenicToolAssociationService);
	}

	public ObjectResponse<TransgenicToolTransgenicToolAssociation> getAssociation(Long subjectId, String relationName, Long objectId) {
		return transgenicToolTransgenicToolAssociationService.getAssociation(subjectId, relationName, objectId);
	}

	@Override
	public APIResponse updateTransgenicToolTransgenicToolAssociations(String dataProvider, List<TransgenicToolTransgenicToolAssociationDTO> associationData) {
		return transgenicToolTransgenicToolAssociationExecutor.runLoadApi(transgenicToolTransgenicToolAssociationService, dataProvider, associationData);
	}
}
