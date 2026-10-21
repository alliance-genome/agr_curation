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

	async getGeneAssociationsForAllele(alleleId) {
		return this.api.get(`/allele/${alleleId}/gene-associations`);
	}

	/**
	 * Replaces an allele's gene associations with the submitted list. An entry with an id updates that
	 * association, one without creates one, and any the list omits are deleted. An empty list deletes them all.
	 */
	async replaceGeneAssociationsForAllele(alleleId, associations) {
		return this.api.put(`/allele/${alleleId}/gene-associations`, associations);
	}

	saveAlleleGeneAssociations(updatedAssociations) {
		updatedAssociations.forEach((updatedAssociation) => {
			this.saveAlleleGeneAssociation(updatedAssociation);
		});
	}
}
