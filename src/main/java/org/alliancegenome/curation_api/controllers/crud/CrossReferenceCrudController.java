package org.alliancegenome.curation_api.controllers.crud;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.interfaces.crud.CrossReferenceCrudInterface;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.services.CrossReferenceService;

@RequestScoped
public class CrossReferenceCrudController extends BaseCrudController<CrossReferenceService, CrossReference, CrossReferenceDAO> implements CrossReferenceCrudInterface {

	@Inject CrossReferenceService crossReferenceService;


	@Override
	@PostConstruct
	protected void init() {
		setService(crossReferenceService);
	}
}
