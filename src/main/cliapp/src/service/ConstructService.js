import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';

export class ConstructService extends BaseAuthService {
	saveConstruct(updatedConstruct) {
		return this.api.put(`/construct`, updatedConstruct);
	}

	createConstruct(newConstruct) {
		return this.api.post(`/construct`, newConstruct);
	}

	async deleteConstruct(construct) {
		const deletionService = new DeletionService();
		return await deletionService.delete(Endpoints.Entity.CONSTRUCT, construct.id);
	}

	async getConstruct(identifier) {
		return this.api.get(`/construct/${identifier}`);
	}
}
