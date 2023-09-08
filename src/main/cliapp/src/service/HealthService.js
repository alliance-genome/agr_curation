import { BaseService } from './BaseService';

export class HealthService extends BaseService {

		getHealth() {
			return this.api.get('/health').then(res => res.data);
		}
}
