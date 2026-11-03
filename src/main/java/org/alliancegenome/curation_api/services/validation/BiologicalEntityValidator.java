package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.services.validation.base.SubmittedObjectValidator;

public class BiologicalEntityValidator<E extends BiologicalEntity> extends SubmittedObjectValidator<E> {

	public E validateBiologicalEntityFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet) {
		return validateBiologicalEntityFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, true);
	}

	public E validateBiologicalEntityFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet, boolean requireModIdentifier) {
		return validateBiologicalEntityFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, requireModIdentifier, true);
	}

	public E validateBiologicalEntityFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet, boolean requireModIdentifier, boolean manageRelatedNotes) {

		dbEntity = validateSubmittedObjectFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, requireModIdentifier, manageRelatedNotes);

		NCBITaxonTerm taxon = validateRequiredTaxon(uiEntity.getTaxon(), dbEntity.getTaxon());
		dbEntity.setTaxon(taxon);

		return dbEntity;
	}
}
