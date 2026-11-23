package org.alliancegenome.curation_api.services.validation;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.Reagent;
import org.alliancegenome.curation_api.services.validation.base.SubmittedObjectValidator;
import org.apache.commons.collections.CollectionUtils;

public class ReagentValidator extends SubmittedObjectValidator<Reagent> {

	public Reagent validateCommonReagentFields(Reagent uiEntity, Reagent dbEntity, String noteTypeVocabularyTermSet) {
		return validateCommonReagentFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, true);
	}

	/**
	 * @param requireModIdentifier whether the reagent needs a MOD internal or primary external ID. A reagent a
	 *        curator creates has neither; its minted AGRKB curie identifies it (SCRUM-6748).
	 */
	public Reagent validateCommonReagentFields(Reagent uiEntity, Reagent dbEntity, String noteTypeVocabularyTermSet, boolean requireModIdentifier) {
		return validateCommonReagentFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, requireModIdentifier, true);
	}

	/**
	 * @param manageListFields whether the calling endpoint's JSON view carries relatedNotes and secondaryIdentifiers.
	 *        An endpoint whose view omits them leaves the stored ones as they are; they are written through the
	 *        reagent's sub-resources instead.
	 */
	public Reagent validateCommonReagentFields(Reagent uiEntity, Reagent dbEntity, String noteTypeVocabularyTermSet, boolean requireModIdentifier, boolean manageListFields) {

		dbEntity = (Reagent) validateSubmittedObjectFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, requireModIdentifier, manageListFields);

		if (manageListFields) {
			List<String> secondaryIds = CollectionUtils.isNotEmpty(uiEntity.getSecondaryIdentifiers()) ? uiEntity.getSecondaryIdentifiers() : null;
			dbEntity.setSecondaryIdentifiers(secondaryIds);
		}

		// SCRUM-6746: an edit that sends placeholder sets it, as a load does; one that leaves it out
		// keeps the stored flag (the column is not nullable)
		if (uiEntity.getPlaceholder() != null) {
			dbEntity.setPlaceholder(uiEntity.getPlaceholder());
		}

		return dbEntity;
	}
}
