package org.alliancegenome.curation_api.services.helpers.associations;

import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AssociationIdentityHelper {
	
	@Inject ReferenceService refService;
	
	public static String alleleGeneAssociationIdentity(AlleleGeneAssociation association) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(association.getAlleleAssociationSubject().getIdentifier());
		uniqueId.add(association.getRelation().getName());
		uniqueId.add(association.getAlleleGeneAssociationObject().getIdentifier());
		
		return uniqueId.getUniqueId();
	}
}
