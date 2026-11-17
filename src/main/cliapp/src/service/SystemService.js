import { BaseAuthService } from './BaseAuthService';

export class SystemService extends BaseAuthService {
	getSiteSummary() {
		return this.api.get('/system/sitesummary').then((res) => res.data);
	}

	reindex(endpoint) {
		return this.api.get(`/${endpoint}/reindex`);
	}
}
