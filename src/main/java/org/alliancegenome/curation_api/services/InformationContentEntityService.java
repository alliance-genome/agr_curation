package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.InformationContentEntityDAO;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.services.base.CurieObjectCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class InformationContentEntityService extends CurieObjectCrudService<InformationContentEntity, InformationContentEntityDAO> {

	@Inject
	InformationContentEntityDAO informationContentEntityDAO;
	@Inject
	ReferenceService referenceService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(informationContentEntityDAO);
	}

	public InformationContentEntity retrieveFromDbOrLiteratureService(String curieOrXref) {
		InformationContentEntity ice = informationContentEntityDAO.findByCurie(curieOrXref);
		if (ice == null)
			ice = referenceService.retrieveFromDbOrLiteratureService(curieOrXref);

		return ice;
	}
}
