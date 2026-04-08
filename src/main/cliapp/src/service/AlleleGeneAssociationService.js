import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';

export class AlleleGeneAssociationService extends BaseAuthService {
	saveAlleleGeneAssociation(updatedAssociation) {
		return this.api.put(`/allelegeneassociation`, updatedAssociation);
	}

	createAlleleGeneAssociation(updatedAssociation) {
		return this.api.post(`/allelegeneassociation`, updatedAssociation);
	}

	async deleteAlleleGeneAssociation(id) {
		const deletionService = new DeletionService();
		return await deletionService.delete(Endpoints.Entity.ALLELE_GENE_ASSOCIATION, id);
	}

	saveAlleleGeneAssociations(updatedAssociations) {
		updatedAssociations.forEach((updatedAssociation) => {
			this.saveAlleleGeneAssociation(updatedAssociation);
		});
	}
}
