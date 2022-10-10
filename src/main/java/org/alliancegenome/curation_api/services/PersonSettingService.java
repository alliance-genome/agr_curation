package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.PersonSettingDAO;
import org.alliancegenome.curation_api.model.entities.PersonSetting;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@RequestScoped
public class PersonSettingService extends BaseEntityCrudService<PersonSetting, PersonSettingDAO> {

	@Inject PersonSettingDAO personSettingDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(personSettingDAO);
	}

}
