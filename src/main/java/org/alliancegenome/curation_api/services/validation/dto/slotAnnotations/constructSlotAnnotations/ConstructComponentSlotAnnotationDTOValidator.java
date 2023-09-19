package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.constructSlotAnnotations.ConstructComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.dto.NoteDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class ConstructComponentSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator {

	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	@Inject
	ConstructComponentSlotAnnotationDAO constructComponentDAO;
	@Inject
	NoteDTOValidator noteDtoValidator;
	@Inject
	NoteDAO noteDAO;
	
	public ObjectResponse<ConstructComponentSlotAnnotation> validateConstructComponentSlotAnnotationDTO(ConstructComponentSlotAnnotation annotation, ConstructComponentSlotAnnotationDTO dto) {
		ObjectResponse<ConstructComponentSlotAnnotation> ccsaResponse = new ObjectResponse<ConstructComponentSlotAnnotation>();

		ObjectResponse<ConstructComponentSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		ccsaResponse.addErrorMessages(saResponse.getErrorMessages());

		if (StringUtils.isAllBlank(dto.getComponentSymbol())) {
			ccsaResponse.addErrorMessage("component_symbol", ValidationConstants.REQUIRED_MESSAGE);;
		} else {
			annotation.setComponentSymbol(dto.getComponentSymbol());
		}
		
		if (StringUtils.isNotBlank(dto.getTaxonCurie())) {
			ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.get(dto.getTaxonCurie());
			if (taxonResponse.getEntity() == null) {
				ccsaResponse.addErrorMessage("taxon_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTaxonCurie() + ")");
			}
			annotation.setTaxon(taxonResponse.getEntity());
		} else {
			annotation.setTaxon(null);
		}
		
		if (StringUtils.isNotBlank(dto.getTaxonText())) {
			annotation.setTaxonText(dto.getTaxonText());
		} else {
			annotation.setTaxonText(null);
		}
		
		if (CollectionUtils.isNotEmpty(annotation.getRelatedNotes())) {
			annotation.getRelatedNotes().forEach(note -> {
				constructComponentDAO.deleteAttachedNote(note.getId());
			});
		}
		if (CollectionUtils.isNotEmpty(dto.getNoteDtos())) {
			List<Note> notes = new ArrayList<>();
			Set<String> noteIdentities = new HashSet<>();
			for (NoteDTO noteDTO : dto.getNoteDtos()) {
				ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(noteDTO, VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY);
				if (noteResponse.hasErrors()) {
					ccsaResponse.addErrorMessage("note_dtos", noteResponse.errorMessagesString());
					break;
				}
				String noteIdentity = NoteIdentityHelper.noteDtoIdentity(noteDTO);
				if (!noteIdentities.contains(noteIdentity)) {
					noteIdentities.add(noteIdentity);
					notes.add(noteDAO.persist(noteResponse.getEntity()));
				}
			}
			annotation.setRelatedNotes(notes);
		} else {
			annotation.setRelatedNotes(null);
		}
		
		ccsaResponse.setEntity(annotation);

		return ccsaResponse;
	}
}
