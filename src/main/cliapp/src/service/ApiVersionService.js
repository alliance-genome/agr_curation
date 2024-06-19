import { BaseAuthService } from './BaseAuthService';

export class ApiVersionService extends BaseAuthService {
	getApiVersion() {
		return this.api.get('/version').then((res) => res.data);
	}
}
