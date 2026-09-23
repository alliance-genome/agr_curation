package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.services.CrossReferenceService;

import jakarta.inject.Inject;

public class GenomicEntityValidator<E extends GenomicEntity> extends BiologicalEntityValidator<E> {

	@Inject CrossReferenceValidator crossReferenceValidator;
	@Inject CrossReferenceService crossReferenceService;

	public E validateGenomicEntityFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet) {
		return validateGenomicEntityFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, true);
	}

	public E validateGenomicEntityFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet, boolean requireModIdentifier) {
		return validateGenomicEntityFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, requireModIdentifier, true);
	}

	/**
	 * @param manageCrossReferences whether the calling endpoint's JSON view carries crossReferences. An endpoint whose
	 *        view omits the field receives an empty list whatever the client sent, so applying it clears the stored rows
	 *        and orphanRemoval deletes them; such an endpoint passes false and leaves the list untouched, and cross
	 *        references for that entity are written through its cross-references sub-resource. An endpoint whose view
	 *        does carry the field passes true, both to honour the payload and because clearing the collection
	 *        initialises it - a lazy collection left uninitialised while in view throws once the transaction closes.
	 */
	public E validateGenomicEntityFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet, boolean requireModIdentifier, boolean manageCrossReferences) {

		dbEntity = validateBiologicalEntityFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, requireModIdentifier);

		if (manageCrossReferences) {
			List<CrossReference> xrefs = validateCrossReferences(uiEntity, dbEntity);
			if (dbEntity.getCrossReferences() != null) {
				dbEntity.getCrossReferences().clear();
			}
			if (xrefs != null) {
				if (dbEntity.getCrossReferences() == null) {
					dbEntity.setCrossReferences(new ArrayList<>());
				}
				dbEntity.getCrossReferences().addAll(xrefs);
			}
		}

		return dbEntity;
	}

	public List<CrossReference> validateCrossReferences(E uiEntity, E dbEntity) {
		return crossReferenceValidator.validateCrossReferences(uiEntity.getCrossReferences(), "crossReferences", response);
	}

}
