import { BaseAuthService } from './BaseAuthService';

export class LoggedInPersonService extends BaseAuthService {

	getUserInfo() {
		return this.api.get(`/loggedinperson`).then(res => res.data);
	}

	regenApiToken() {
		return this.api.get(`/loggedinperson/regenapitoken`).then(res => res.data);
	}

	saveUserSettings(settings) {
		return this.api.post(`/loggedinperson/savesettings`, settings).then(res => res.data);
	}
}
