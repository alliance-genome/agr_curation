import { BaseAuthService } from './BaseAuthService';

export class CassetteService extends BaseAuthService {
	saveCassette(updatedCassette) {
		return this.api.put(`/cassette`, updatedCassette);
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
