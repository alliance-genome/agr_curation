import { BaseAuthService } from './BaseAuthService';

export class OntologyService extends BaseAuthService {

	endpoint;

	constructor(serviceEndpoint) {
		super();
		this.endpoint = serviceEndpoint;
	}

	getRootNodes() {
		return this.api.get(`/${this.endpoint}/rootNodes`);
	}

	getChildren(id) {
		return this.api.get(`/${this.endpoint}/${id}/children`);
	}

	getTerm(id) {
		return this.api.get(`/${this.endpoint}/${id}`);
	}
}
