import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';

export class AlleleGeneAssociationService extends BaseAuthService {
	saveAlleleGeneAssociation(updatedAssociation) {
		return this.api.put(`/allelegeneassociation`, updatedAssociation);
	}

	createAlleleGeneAssociation(updatedAssociation) {
		return this.api.post(`/allelegeneassociation`, updatedAssociation);
	}

	async deleteAlleleGeneAssociation(id) {
		const deletionService = new DeletionService();
		return await deletionService.delete(`allelegeneassociation`, id);
	}

	saveAlleleGeneAssociations(updatedAssociations) {
		updatedAssociations.forEach((updatedAssociation) => {
			this.saveAlleleGeneAssociation(updatedAssociation);
		});
	}
}
