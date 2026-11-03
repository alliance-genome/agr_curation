import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';

export class GeneService extends BaseAuthService {
	saveGene(updatedGene) {
		return this.api.put(`/gene`, updatedGene);
	}

	createGene(newGene) {
		return this.api.post(`/gene`, newGene);
	}

	async deleteGene(gene) {
		const deletionService = new DeletionService();
		return await deletionService.delete(Endpoints.Entity.GENE, gene.id);
	}

	async getGene(identifier) {
		return this.api.get(`/gene/${identifier}`);
	}
}
