import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';
import { getIdentifier } from '../utils/utils';

export class CassetteService extends BaseAuthService {
	getCassette(identifier) {
		return this.api.get(`/cassette/${identifier}`);
	}

	saveCassette(updatedCassette) {
		return this.api.put(`/cassette`, updatedCassette);
	}

	createCassette(newCassette) {
		return this.api.post(`/cassette`, newCassette);
	}

	/**
	 * Deletes the cassette (SCRUM-6749). The API refuses a cassette that a construct uses. A cassette without any
	 * identifier is deleted by its database ID.
	 *
	 * @returns {Promise<{isError: boolean, message?: string}>} the outcome, with the API's reason for a refusal
	 */
	async deleteCassette(cassette) {
		const deletionService = new DeletionService();
		return await deletionService.delete(
			Endpoints.Entity.CASSETTE,
			cassette.curie || getIdentifier(cassette) || cassette.id
		);
	}

	/**
	 * One of the cassette's component lists (SCRUM-6752): `components`, `genomic-entity-associations`,
	 * `transgenic-tool-associations` or `str-associations`.
	 */
	getComponentList(cassetteId, list) {
		return this.api.get(`/cassette/${cassetteId}/${list}`);
	}

	/**
	 * Replaces one of the cassette's component lists with the submitted rows: a row with an id updates that row, one
	 * without creates one, and rows the list omits are deleted.
	 */
	replaceComponentList(cassetteId, list, rows) {
		return this.api.put(`/cassette/${cassetteId}/${list}`, rows);
	}
}
