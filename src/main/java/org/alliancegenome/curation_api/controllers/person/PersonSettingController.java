package org.alliancegenome.curation_api.controllers.person;

import java.util.HashMap;
import java.util.Map;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.interfaces.person.PersonSettingInterface;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.PersonSetting;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.PersonSettingService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class PersonSettingController implements PersonSettingInterface {

	@Inject
	@AuthenticatedUser
	Person authenticatedPerson;
	@Inject
	PersonSettingService personSettingService;
	@Inject
	PersonService personService;

	@Override
	public ObjectResponse<PersonSetting> getUserSetting(String settingsKey) {
		return new ObjectResponse<>(getSetting(settingsKey));
	}

	@Override
	@Transactional
	public ObjectResponse<PersonSetting> deleteUserSetting(String settingsKey) {
		PersonSetting setting = getSetting(settingsKey);
		personSettingService.delete(setting.getId());
		return new ObjectResponse<>(setting);
	}

	@Override
	@Transactional
	public ObjectResponse<PersonSetting> saveUserSetting(String settingsKey, Map<String, Object> settingsMap) {

		PersonSetting setting = getSetting(settingsKey);

		if (setting == null) {
			setting = new PersonSetting();
			ObjectResponse<Person> personResp = personService.get(authenticatedPerson.getId());
			Person person = personResp.getEntity();
			setting.setPerson(person);
			setting.setUpdatedBy(person);
			setting.setCreatedBy(person);
			setting.setSettingsKey(settingsKey);
			setting.setSettingsMap(settingsMap);
			return personSettingService.create(setting);
		} else {
			setting.setSettingsMap(settingsMap);
			return personSettingService.update(setting);
		}
		
	}

	private PersonSetting getSetting(String settingsKey) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("person.id", authenticatedPerson.getId());
		params.put("settingsKey", settingsKey);
		SearchResponse<PersonSetting> resp = personSettingService.findByParams(new Pagination(0, 20), params);
		if (resp.getResults().size() >= 1) {
			return resp.getResults().get(0);
		} else {
			return null;
		}
	}

}