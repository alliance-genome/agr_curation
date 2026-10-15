import { BaseAuthService } from './BaseAuthService';

export class ReferenceService extends BaseAuthService {
	async getReferencesForAllele(alleleId) {
		return this.api.get(`/allele/${alleleId}/references`);
	}

	/**
	 * Replaces an allele's references with the submitted list, detaching any that are omitted.
	 * An empty list clears them all.
	 */
	async replaceReferencesForAllele(alleleId, references) {
		return this.api.put(`/allele/${alleleId}/references`, references);
	}
}
