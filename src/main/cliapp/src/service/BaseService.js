import { createApiClient } from './ApiClient';

export class BaseService {
	api;

	constructor() {
		this.api = createApiClient({
			baseURL: '/',
		});
	}
}
