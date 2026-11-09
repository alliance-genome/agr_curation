import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';
import { getIdentifier } from '../utils/utils';

export class GeneService extends BaseAuthService {
	saveGene(updatedGene) {
		return this.api.put(`/gene`, updatedGene);
	}

	saveGeneDetail(updatedGene) {
		return this.api.put(`/gene/updateDetail`, updatedGene);
	}

	createGene(newGene) {
		return this.api.post(`/gene`, newGene);
	}

	async deleteGene(gene) {
		const deletionService = new DeletionService();
		// the curie resolves to exactly one gene; a primary external ID can equal another gene's MOD internal ID
		return await deletionService.delete(Endpoints.Entity.GENE, gene.curie || getIdentifier(gene));
	}

	async getGene(identifier) {
		return this.api.get(`/gene/${identifier}`);
	}
}
