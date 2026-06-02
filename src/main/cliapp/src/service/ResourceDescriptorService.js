import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';

export class ResourceDescriptorService extends BaseAuthService {
	saveResourceDescriptor(updatedResourceDescriptor) {
		return this.api.put(`/resourcedescriptor`, updatedResourceDescriptor);
	}

	createResourceDescriptor(resourceDescriptor) {
		return this.api.post(`/resourcedescriptor`, resourceDescriptor);
	}

	async deleteResourceDescriptor(resourceDescriptor) {
		const deletionService = new DeletionService();
		return await deletionService.delete(Endpoints.Resource.DESCRIPTOR, resourceDescriptor.id);
	}
}
