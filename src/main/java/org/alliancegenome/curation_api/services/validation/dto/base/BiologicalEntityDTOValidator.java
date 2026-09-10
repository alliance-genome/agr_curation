package org.alliancegenome.curation_api.services.validation.dto.base;

import java.util.Objects;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.BiologicalEntityDTO;

public class BiologicalEntityDTOValidator<E extends BiologicalEntity, D extends BiologicalEntityDTO> extends SubmittedObjectDTOValidator<E, D> {

	public E validateBiologicalEntityDTO(E entity, D dto, Species beSpecies, String noteTypeVocabularyTermSet) {

		entity = validateSubmittedObjectDTO(entity, dto, noteTypeVocabularyTermSet);

		NCBITaxonTerm taxon = validateRequiredTaxon("taxon_curie", dto.getTaxonCurie());
		if (beSpecies != null && (beSpecies.getDisplayName().equals("RGD") || beSpecies.getDisplayName().equals("HUMAN")) && !Objects.equals(taxon.getCurie(), beSpecies.getTaxon().getCurie())) {
			response.addErrorMessage("taxon_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTaxonCurie() + ") for " + beSpecies.getDisplayName() + " load");
		}
		entity.setTaxon(taxon);

		return entity;
	}

}
