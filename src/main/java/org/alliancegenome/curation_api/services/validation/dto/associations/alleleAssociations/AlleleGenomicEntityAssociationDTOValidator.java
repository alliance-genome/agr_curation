package org.alliancegenome.curation_api.services.validation.dto.associations.alleleAssociations;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;
import org.alliancegenome.curation_api.services.validation.dto.NoteDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.EvidenceAssociationDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleGenomicEntityAssociationDTOValidator extends EvidenceAssociationDTOValidator {

	@Inject NoteDTOValidator noteDtoValidator;
	@Inject EcoTermService ecoTermService;

	public <E extends AlleleGenomicEntityAssociation, D extends AlleleGenomicEntityAssociationDTO> ObjectResponse<E> validateAlleleGenomicEntityAssociationDTO(E association, D dto) {
		ObjectResponse<E> assocResponse = validateEvidenceAssociationDTO(association, dto);
		association = assocResponse.getEntity();

		if (StringUtils.isNotBlank(dto.getEvidenceCodeCurie())) {
			ECOTerm ecoTerm = ecoTermService.findByCurieOrSecondaryId(dto.getEvidenceCodeCurie());
			if (ecoTerm == null) {
				assocResponse.addErrorMessage("evidence_code_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getEvidenceCodeCurie() + ")");
			} else if (!ecoTerm.getSubsets().contains(OntologyConstants.AGR_ECO_TERM_SUBSET)) {
				assocResponse.addErrorMessage("evidence_code_curie", ValidationConstants.UNSUPPORTED_MESSAGE + " (" + dto.getEvidenceCodeCurie() + ")");
			} else {
				association.setEvidenceCode(ecoTerm);
			}
		} else {
			association.setEvidenceCode(null);
		}

		if (dto.getNoteDto() != null) {
			ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(dto.getNoteDto(), VocabularyConstants.ALLELE_GENOMIC_ENTITY_ASSOCIATION_NOTE_TYPES_VOCABULARY_TERM_SET);
			if (noteResponse.hasErrors()) {
				assocResponse.addErrorMessage("note_dto", noteResponse.errorMessagesString());
			} else {
				association.setRelatedNote(noteResponse.getEntity());
			}
		} else {
			association.setRelatedNote(null);
		}

		assocResponse.setEntity(association);

		return assocResponse;
	}
}
