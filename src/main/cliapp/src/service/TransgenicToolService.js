import { BaseAuthService } from './BaseAuthService';

export class TransgenicToolService extends BaseAuthService {
	async getTransgenicTool(identifier) {
		return this.api.get(`/transgenic-tool/${identifier}`);
	}

	async saveTransgenicTool(transgenicTool) {
		return this.api.put(`/transgenic-tool`, transgenicTool);
	}

	async getTransgenicToolAssociations(transgenicToolId) {
		return this.api.get(`/transgenic-tool/${transgenicToolId}/transgenic-tool-associations`);
	}

	/**
	 * Replaces the associations the tool is the subject of with the submitted list. An entry with an id updates
	 * that association, one without creates one, and any the list omits are deleted.
	 */
	async replaceTransgenicToolAssociations(transgenicToolId, associations) {
		return this.api.put(`/transgenic-tool/${transgenicToolId}/transgenic-tool-associations`, associations);
	}
}
