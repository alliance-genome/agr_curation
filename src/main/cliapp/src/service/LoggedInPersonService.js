import { BaseAuthService } from './BaseAuthService';

export class LoggedInPersonService extends BaseAuthService {

	getUserInfo() {
		return this.api.get(`/loggedinperson`).then(res => res.data);
	}
}
