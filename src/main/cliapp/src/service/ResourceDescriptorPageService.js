import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';

export class ResourceDescriptorPageService extends BaseAuthService {
	saveResourceDescriptorPage(updatedResourceDescriptorPage) {
		return this.api.put(`/resourcedescriptorpage`, updatedResourceDescriptorPage);
	}

	createResourceDescriptorPage(resourceDescriptorPage) {
		return this.api.post(`/resourcedescriptorpage`, resourceDescriptorPage);
	}

	async deleteResourceDescriptorPage(resourceDescriptorPage) {
		const deletionService = new DeletionService();
		return await deletionService.delete(Endpoints.Resource.DESCRIPTOR_PAGE, resourceDescriptorPage.id);
	}
}
