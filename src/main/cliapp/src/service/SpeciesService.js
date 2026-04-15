import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';

export class SpeciesService extends BaseAuthService {
	saveSpecies(updatedSpecies) {
		return this.api.put(`/species`, updatedSpecies);
	}

	async deleteSpecies(species) {
		const deletionService = new DeletionService();
		return await deletionService.delete(Endpoints.Entity.SPECIES, species.id);
	}
}
