import { BaseAuthService } from './BaseAuthService';

export class PersonSettingsService extends BaseAuthService {
	getUserSettings(settingsKey) {
		return this.api.get(`/personsettings/${settingsKey}`).then(res => res.data);
	}

	saveUserSettings(settingsKey, settingsMap) {
		return this.api.put(`/personsettings/${settingsKey}`, settingsMap).then(res => res.data);
	}

	deleteUserSettings(settingsKey) {
		return this.api.delete(`/personsettings/${settingsKey}`).then(res => res.data);
	}
}
