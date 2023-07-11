package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.PersonSetting;

@ApplicationScoped
public class PersonSettingDAO extends BaseSQLDAO<PersonSetting> {

	protected PersonSettingDAO() {
		super(PersonSetting.class);
	}

}
