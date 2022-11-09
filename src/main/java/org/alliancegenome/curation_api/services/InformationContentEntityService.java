package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.InformationContentEntityDAO;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@RequestScoped
public class InformationContentEntityService extends BaseEntityCrudService<InformationContentEntity, InformationContentEntityDAO> {

	@Inject InformationContentEntityDAO informationContentEntityDAO;
	@Inject ReferenceService referenceService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(informationContentEntityDAO);
	}
	
	public InformationContentEntity retrieveFromDbOrLiteratureService(String curieOrXref) {
		InformationContentEntity ice = informationContentEntityDAO.find(curieOrXref);
		if (ice == null)
			ice = referenceService.retrieveFromDbOrLiteratureService(curieOrXref);
		
		return ice;
	}
}
