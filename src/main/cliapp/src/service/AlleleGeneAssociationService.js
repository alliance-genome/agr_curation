import { BaseAuthService } from './BaseAuthService';

export class AlleleGeneAssociationService extends BaseAuthService {
  saveAlleleGeneAssociation(updatedAssociation) {
    return this.api.put(`/allelegeneassociation`, updatedAssociation);
  }

  createAlleleGeneAssociation(updatedAssociation) {
    return this.api.post(`/allelegeneassociation`, updatedAssociation);
  }

  saveAlleleGeneAssociations(updatedAssociations) {
    updatedAssociations.forEach(updatedAssociation => {
      this.saveAlleleGeneAssociation(updatedAssociation);
    });
  }
}