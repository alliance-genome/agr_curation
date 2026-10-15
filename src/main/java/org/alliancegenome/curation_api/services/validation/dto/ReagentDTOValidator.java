package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.model.entities.Reagent;
import org.alliancegenome.curation_api.model.ingest.dto.ReagentDTO;
import org.alliancegenome.curation_api.services.validation.dto.base.SubmittedObjectDTOValidator;

public class ReagentDTOValidator<E extends Reagent, D extends ReagentDTO> extends SubmittedObjectDTOValidator<E, D> {

	public E validateReagentDTO(E reagent, D dto, String noteTypeVocabularyTermSet) {
		reagent = validateSubmittedObjectDTO(reagent, dto, noteTypeVocabularyTermSet);
		
		reagent.setSecondaryIdentifiers(handleStringListField(dto.getSecondaryIdentifiers()));

		// Only a submitted placeholder changes the flag. MGI's files leave it out, and the MGI
		// constructs flagged by v0.43.0.3 must stay hidden; a new reagent starts as false.
		if (dto.getPlaceholder() != null) {
			reagent.setPlaceholder(dto.getPlaceholder());
		}

		return reagent;
	}
}
