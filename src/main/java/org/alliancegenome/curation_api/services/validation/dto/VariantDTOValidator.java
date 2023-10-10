package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class VariantDTOValidator extends BaseDTOValidator {

	@Inject
	VariantDAO variantDAO;
	@Inject
	NoteDAO noteDAO;
	@Inject
	NoteDTOValidator noteDtoValidator;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	SoTermService soTermService;

	@Transactional
	public Variant validateVariantDTO(VariantDTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {

		ObjectResponse<Variant> variantResponse = new ObjectResponse<Variant>();

		Variant variant = null;
		if (StringUtils.isBlank(dto.getCurie())) {
			variantResponse.addErrorMessage("curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			variant = variantDAO.find(dto.getCurie());
		}

		if (variant == null)
			variant = new Variant();

		variant.setCurie(dto.getCurie());

		ObjectResponse<Variant> geResponse = validateGenomicEntityDTO(variant, dto, dataProvider);
		variantResponse.addErrorMessages(geResponse.getErrorMessages());

		variant = geResponse.getEntity();
		
		SOTerm variantType = null;
		if (StringUtils.isBlank(dto.getVariantTypeCurie())) {
			variantResponse.addErrorMessage("variant_type_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			variantType = soTermService.findByCurieOrSecondaryId(dto.getVariantTypeCurie());
			if (variantType == null)
				variantResponse.addErrorMessage("variant_type_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getVariantTypeCurie() + ")");
		}
		variant.setVariantType(variantType);

		VocabularyTerm variantStatus = null;
		if (StringUtils.isNotBlank(dto.getVariantStatusName())) {
			variantStatus = vocabularyTermService.getTermInVocabulary(VocabularyConstants.VARIANT_STATUS_VOCABULARY, dto.getVariantStatusName()).getEntity();
			if (variantStatus == null) {
				variantResponse.addErrorMessage("variant_status_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getVariantStatusName() + ")");
			}
		}
		variant.setVariantStatus(variantStatus);
		
		SOTerm sourceGeneralConsequence = null;
		if (!StringUtils.isBlank(dto.getVariantTypeCurie())) {
			variantType = soTermService.findByCurieOrSecondaryId(dto.getVariantTypeCurie());
			if (variantType == null)
				variantResponse.addErrorMessage("source_general_consequence_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSourceGeneralConsequenceCurie() + ")");
		}
		variant.setSourceGeneralConsequence(sourceGeneralConsequence);

		if (CollectionUtils.isNotEmpty(variant.getRelatedNotes())) {
			variant.getRelatedNotes().forEach(note -> {
				variantDAO.deleteAttachedNote(note.getId());
			});
		}
		if (CollectionUtils.isNotEmpty(dto.getNoteDtos())) {
			List<Note> notes = new ArrayList<>();
			Set<String> noteIdentities = new HashSet<>();
			for (NoteDTO noteDTO : dto.getNoteDtos()) {
				ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(noteDTO, VocabularyConstants.VARIANT_NOTE_TYPES_VOCABULARY_TERM_SET);
				if (noteResponse.hasErrors()) {
					variantResponse.addErrorMessage("note_dtos", noteResponse.errorMessagesString());
					break;
				}
				String noteIdentity = NoteIdentityHelper.noteDtoIdentity(noteDTO);
				if (!noteIdentities.contains(noteIdentity)) {
					noteIdentities.add(noteIdentity);
					notes.add(noteDAO.persist(noteResponse.getEntity()));
				}
			}
			variant.setRelatedNotes(notes);
		} else {
			variant.setRelatedNotes(null);
		}

		if (variantResponse.hasErrors())
			throw new ObjectValidationException(dto, variantResponse.errorMessagesString());
		
		variant = variantDAO.persist(variant);

		return variant;
	}

}
