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
import org.alliancegenome.curation_api.services.CurieMintService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VariantValidator extends GenomicEntityValidator<Variant> {

	@Inject VariantDAO variantDAO;
	@Inject CurieMintService curieMintService;
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

		// SCRUM-6077: mint an AGRKB curie for a NEW variant that has none, set before persist so the
		// curie is written by the same insert. A curator-supplied curie is left alone.
		//
		// The getId() == null guard is load-bearing, as it is for alleles, AGMs and genes:
		// validateVariant is shared by validateVariantCreate and validateVariantUpdate, and the
		// field-copy chain above (validateGenomicEntityFields -> ... ->
		// SubmittedObjectValidator.validateSubmittedObjectFields) assigns
		// dbEntity.setCurie(handleStringField(uiEntity.getCurie())) unconditionally. So an update whose
		// payload omits curie nulls it; without this guard the mint would then issue a fresh curie and
		// the variant's AGRKB id would silently change on every such update.
		if (dbEntity.getId() == null) {
			curieMintService.mintCurieIfAbsent(dbEntity);
		}
		dbEntity = variantDAO.persist(dbEntity);

		return dbEntity;
	}

}
