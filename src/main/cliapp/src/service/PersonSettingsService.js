import { BaseAuthService } from './BaseAuthService';

export class PersonSettingsService extends BaseAuthService {
	getUserSettings(key) {
		return this.api.get(`/personsettings/${key}`).then(res => res.data);
	}

	saveUserSettings(key, settings) {
		return this.api.put(`/personsettings/${key}`, settings).then(res => res.data);
	}
}
