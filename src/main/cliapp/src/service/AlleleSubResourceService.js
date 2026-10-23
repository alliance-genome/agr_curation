import { BaseAuthService } from './BaseAuthService';

/**
 * The lists an allele holds through sub-resources of its own, such as `/allele/{id}/crossreferences`.
 */
export class AlleleSubResourceService extends BaseAuthService {
	/**
	 * @param {number} alleleId
	 * @param {string} path the sub-resource's path under the allele, such as `'crossreferences'`
	 */
	async getList(alleleId, path) {
		return this.api.get(`/allele/${alleleId}/${path}`);
	}

	/**
	 * Replaces the allele's list with the submitted rows; an empty list clears it. Cross references and gene
	 * associations are created, updated or deleted to match. References are existing ones the allele is linked
	 * to, and any the list omits are detached.
	 *
	 * @param {number} alleleId
	 * @param {string} path the sub-resource's path under the allele, such as `'crossreferences'`
	 * @param {Object[]} rows
	 */
	async replaceList(alleleId, path, rows) {
		return this.api.put(`/allele/${alleleId}/${path}`, rows);
	}
}
