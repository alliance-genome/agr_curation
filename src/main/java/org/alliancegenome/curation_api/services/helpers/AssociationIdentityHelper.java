package org.alliancegenome.curation_api.services.helpers;

import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.services.ReferenceService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AssociationIdentityHelper {
	
	@Inject ReferenceService refService;
	
	public static String alleleConstructAssociationIdentity(AlleleConstructAssociation association) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(association.getAlleleAssociationSubject().getIdentifier());
		uniqueId.add(association.getRelation().getName());
		uniqueId.add(association.getAlleleConstructAssociationObject().getIdentifier());
		
		return uniqueId.getUniqueId();
	}
	
	public static String alleleGeneAssociationIdentity(AlleleGeneAssociation association) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(association.getAlleleAssociationSubject().getIdentifier());
		uniqueId.add(association.getRelation().getName());
		uniqueId.add(association.getAlleleGeneAssociationObject().getIdentifier());
		
		return uniqueId.getUniqueId();
	}
}
