import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';

export class ConstructService extends BaseAuthService {
	saveConstruct(updatedConstruct) {
		return this.api.put(`/construct`, updatedConstruct);
	}

	createConstruct(newConstruct) { 
		return this.api.post(`/construct`, newConstruct);
	}

	async deleteConstruct(construct) { 
		const deletionService = new DeletionService();
		return await deletionService.delete(`construct`, construct.id);
	}

	async getConstruct(identifier) { 
		return this.api.get(`/allele/findBy/${identifier}`);
	}
}

