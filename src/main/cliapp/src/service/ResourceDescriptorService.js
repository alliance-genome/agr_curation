import { BaseAuthService } from './BaseAuthService';

export class ResourceDescriptorService extends BaseAuthService {
	saveResourceDescriptor(updatedResourceDescriptor) {
		return this.api.put(`/resourcedescriptor`, updatedResourceDescriptor);
	}
}
