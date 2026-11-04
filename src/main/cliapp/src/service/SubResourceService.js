import { BaseAuthService } from './BaseAuthService';

/**
 * The lists an entity holds through sub-resources of its own, such as `/allele/{id}/crossreferences`.
 */
export class SubResourceService extends BaseAuthService {
	/**
	 * @param {string} entityPath the entity's path segment, such as `'allele'`
	 */
	constructor(entityPath) {
		super();
		this.entityPath = entityPath;
	}

	/**
	 * @param {number} ownerId
	 * @param {string} path the sub-resource's path under the entity, such as `'crossreferences'`
	 */
	async getList(ownerId, path) {
		return this.api.get(`/${this.entityPath}/${ownerId}/${path}`);
	}

	/**
	 * Replaces the entity's list with the submitted rows; an empty list clears it.
	 *
	 * @param {number} ownerId
	 * @param {string} path the sub-resource's path under the entity, such as `'crossreferences'`
	 * @param {Object[]} rows
	 */
	async replaceList(ownerId, path, rows) {
		return this.api.put(`/${this.entityPath}/${ownerId}/${path}`, rows);
	}
}
