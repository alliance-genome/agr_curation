import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';

export class AlleleService extends BaseAuthService {
	saveAllele(updatedAllele) {
		return this.api.put(`/allele`, updatedAllele);
	}

	saveAlleleDetail(updatedAllele) {
		return this.api.put(`/allele/updateDetail`, updatedAllele);
	}

	createAllele(newAllele) {
		return this.api.post(`/allele`, newAllele);
	}

	async deleteAllele(allele) {
		const deletionService = new DeletionService();
		return await deletionService.delete(`allele`, allele.id);
	}

	async getAllele(identifier) {
		return this.api.get(`/allele/${identifier}`);
	}
}
