import { BaseAuthService } from './BaseAuthService';

export class CrossReferenceService extends BaseAuthService {
	async getCrossReferencesForAllele(alleleId) {
		return this.api.get(`/allele/${alleleId}/cross-references`);
	}

	/**
	 * Replaces an allele's cross references with the submitted list, deleting any that are omitted.
	 * An empty list clears them all.
	 */
	async replaceCrossReferencesForAllele(alleleId, crossReferences) {
		return this.api.put(`/allele/${alleleId}/cross-references`, crossReferences);
	}
}
