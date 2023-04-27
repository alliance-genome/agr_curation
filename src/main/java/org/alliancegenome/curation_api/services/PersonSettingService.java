package org.alliancegenome.curation_api.services;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.PersonSettingDAO;
import org.alliancegenome.curation_api.model.entities.PersonSetting;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class PersonSettingService extends BaseEntityCrudService<PersonSetting, PersonSettingDAO> {

	@Inject
	PersonSettingDAO personSettingDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(personSettingDAO);
	}

}
