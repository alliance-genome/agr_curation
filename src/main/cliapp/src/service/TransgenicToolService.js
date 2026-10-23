import { BaseAuthService } from './BaseAuthService';

export class TransgenicToolService extends BaseAuthService {
	async getTransgenicTool(identifier) {
		return this.api.get(`/transgenic-tool/${identifier}`);
	}
}
