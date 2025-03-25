package org.alliancegenome.curation_api.services.validation.associations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.associations.AlleleConstructAssociationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleConstructAssociationValidator extends AlleleGenomicEntityAssociationValidator<AlleleConstructAssociation> {

	@Inject ConstructDAO constructDAO;
	@Inject AlleleConstructAssociationDAO alleleConstructAssociationDAO;

	private String errorMessage;

	public ObjectResponse<AlleleConstructAssociation> validateAlleleConstructAssociation(AlleleConstructAssociation uiEntity) {
		AlleleConstructAssociation constructAssociation = validateAlleleConstructAssociation(uiEntity, false, false);
		response.setEntity(constructAssociation);
		return response;
	}

	public AlleleConstructAssociation validateAlleleConstructAssociation(AlleleConstructAssociation uiEntity, Boolean throwError, Boolean validateAllele) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create/update Allele Construct Association: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleConstructAssociation dbEntity = null;
		if (id != null) {
			dbEntity = alleleConstructAssociationDAO.find(id);
			if (dbEntity == null) {
				addMessageResponse("Could not find Allele Construct Association with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleConstructAssociation();
		}

		dbEntity = (AlleleConstructAssociation) validateAlleleGenomicEntityAssociationFields(uiEntity, dbEntity);

		if (validateAllele) {
			Allele subject = validateRequiredEntity(alleleDAO, "alleleAssociationSubject", uiEntity.getAlleleAssociationSubject(), dbEntity.getAlleleAssociationSubject());
			dbEntity.setAlleleAssociationSubject(subject);
		}

		Construct object = validateObject(uiEntity, dbEntity);
		dbEntity.setAlleleConstructAssociationObject(object);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.ALLELE_CONSTRUCT_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation(), dbEntity.getRelation());
		dbEntity.setRelation(relation);

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorMessage);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}

	private Construct validateObject(AlleleConstructAssociation uiEntity, AlleleConstructAssociation dbEntity) {
		Construct objectEntity = validateRequiredEntity(constructDAO, "alleleConstructAssociationObject", uiEntity.getAlleleConstructAssociationObject(), dbEntity.getAlleleConstructAssociationObject());
		
		if (objectEntity != null) {
			if (objectEntity.getConstructSymbol() != null) {
				if (objectEntity.getConstructSymbol().getEvidence() != null) {
					objectEntity.getConstructSymbol().getEvidence().size();
				}
			}
		}

		return objectEntity;
	}
}
