import { BaseAuthService } from './BaseAuthService';

export class ResourceDescriptorPageService extends BaseAuthService {
	saveResourceDescriptorPage(updatedResourceDescriptorPage) {
		return this.api.put(`/resourcedescriptorpage`, updatedResourceDescriptorPage);
	}
}
