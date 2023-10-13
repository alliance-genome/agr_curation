import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';

export class AlleleService extends BaseAuthService {
	saveAllele(updatedAllele) {
		return this.api.put(`/allele`, updatedAllele);
	}

	createAllele(newAllele) { 
		return this.api.post(`/allele`, newAllele);
	}

	async deleteAllele(allele) { 
		const deletionService = new DeletionService();
		return await deletionService.delete(`allele`, allele.curie);
	}

	async getAllele(curie) { 
		return this.api.get(`/allele/${curie}`);
	}
}

