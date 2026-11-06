import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';
import { getIdentifier } from '../utils/utils';

export class VariantService extends BaseAuthService {
	saveVariant(updatedVariant) {
		return this.api.put(`/variant`, updatedVariant);
	}

	createVariant(newVariant) {
		return this.api.post(`/variant`, newVariant);
	}

	async deleteVariant(variant) {
		const deletionService = new DeletionService();
		return await deletionService.delete(Endpoints.Entity.VARIANT, variant.curie || getIdentifier(variant));
	}

	async getVariant(curie) {
		return this.api.get(`/variant/${curie}`);
	}
}
