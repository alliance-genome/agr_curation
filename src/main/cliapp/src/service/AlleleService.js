import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';
import { getIdentifier } from '../utils/utils';

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
		// the curie resolves to exactly one allele; a primary external ID can equal another allele's MOD internal ID
		return await deletionService.delete(Endpoints.Entity.ALLELE, allele.curie || getIdentifier(allele));
	}

	async getAllele(identifier) {
		return this.api.get(`/allele/${identifier}`);
	}
}
