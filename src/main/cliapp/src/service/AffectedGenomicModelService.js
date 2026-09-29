import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';

export class AffectedGenomicModelService extends BaseAuthService {
	saveAGM(updatedAGM) {
		return this.api.put(`/agm`, updatedAGM);
	}

	createAGM(newAGM) {
		return this.api.post(`/agm`, newAGM);
	}

	async deleteAGM(agm) {
		const deletionService = new DeletionService();
		return await deletionService.delete(Endpoints.Entity.AGM, agm.id);
	}

	async getAGM(identifier) {
		return this.api.get(`/agm/${identifier}`);
	}
}
