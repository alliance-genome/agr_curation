package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.PersonSetting;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersonSettingDAO extends BaseSQLDAO<PersonSetting> {

	protected PersonSettingDAO() {
		super(PersonSetting.class);
	}

}
