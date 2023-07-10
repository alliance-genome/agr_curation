import { BaseAuthService } from './BaseAuthService';

export class PersonService extends BaseAuthService {

	getUserInfo() {
		return this.api.get(`/person`).then(res => res.data);
	}

	regenApiToken() {
		return this.api.get(`/person/regenapitoken`).then(res => res.data);
	}

}
