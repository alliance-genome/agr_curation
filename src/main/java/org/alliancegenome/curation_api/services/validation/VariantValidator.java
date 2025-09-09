package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VariantValidator extends GenomicEntityValidator<Variant> {

	@Inject VariantDAO variantDAO;
	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject SoTermDAO soTermDAO;

	private String errorMessage;

	public Variant validateVariantUpdate(Variant uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Variant: [" + uiEntity.getIdentifier() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Variant ID provided");
			throw new ApiErrorException(response);
		}

		Variant dbEntity = variantDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("id", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}

		dbEntity = (Variant) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateVariant(uiEntity, dbEntity);
	}

	public Variant validateVariantCreate(Variant uiEntity) {
		response = new ObjectResponse<>();
		errorMessage = "Could not create Variant";

		Variant dbEntity = new Variant();

		dbEntity = (Variant) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateVariant(uiEntity, dbEntity);
	}

	public Variant validateVariant(Variant uiEntity, Variant dbEntity) {

		dbEntity = (Variant) validateGenomicEntityFields(uiEntity, dbEntity, VocabularyConstants.VARIANT_NOTE_TYPES_VOCABULARY_TERM_SET);

		SOTerm variantType = validateRequiredEntity(soTermDAO, "variantType", uiEntity.getVariantType(), dbEntity.getVariantType());
		dbEntity.setVariantType(variantType);

		VocabularyTerm variantStatus = validateTermInVocabulary("variantStatus", VocabularyConstants.VARIANT_STATUS_VOCABULARY, uiEntity.getVariantStatus(), dbEntity.getVariantStatus());
		dbEntity.setVariantStatus(variantStatus);

		SOTerm sourceGeneralConsequence = validateEntity(soTermDAO, "sourceGeneralConsequence", uiEntity.getSourceGeneralConsequence(), dbEntity.getSourceGeneralConsequence());
		dbEntity.setSourceGeneralConsequence(sourceGeneralConsequence);

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		dbEntity = variantDAO.persist(dbEntity);

		return dbEntity;
	}

}
