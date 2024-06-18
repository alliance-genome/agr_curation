import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';

export class VariantService extends BaseAuthService {
	saveVariant(updatedVariant) {
		return this.api.put(`/variant`, updatedVariant);
	}

	createVariant(newVariant) {
		return this.api.post(`/variant`, newVariant);
	}

	async deleteVariant(variant) {
		const deletionService = new DeletionService();
		return await deletionService.delete(`variant`, variant.curie);
	}

	async getVariant(curie) {
		return this.api.get(`/variant/${curie}`);
	}
}
