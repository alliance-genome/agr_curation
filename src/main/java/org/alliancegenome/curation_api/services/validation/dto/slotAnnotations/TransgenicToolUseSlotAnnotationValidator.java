package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolUseSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolUseSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TransgenicToolUseSlotAnnotationValidator extends SlotAnnotationValidator<TransgenicToolUseSlotAnnotation> {

	@Inject TransgenicToolUseSlotAnnotationDAO transgenicToolUseDAO;
	@Inject TransgenicToolDAO transgenicToolDAO;

	public ObjectResponse<TransgenicToolUseSlotAnnotation> validateTransgenicToolUseSlotAnnotation(TransgenicToolUseSlotAnnotation uiEntity) {
		TransgenicToolUseSlotAnnotation use = validateTransgenicToolUseSlotAnnotation(uiEntity, false, false);
		response.setEntity(use);
		return response;
	}

	public TransgenicToolUseSlotAnnotation validateTransgenicToolUseSlotAnnotation(TransgenicToolUseSlotAnnotation uiEntity, Boolean throwError, Boolean validateTransgenicTool) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update TransgenicToolUseSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		TransgenicToolUseSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = transgenicToolUseDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find TransgenicToolUseSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new TransgenicToolUseSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (TransgenicToolUseSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateTransgenicTool) {
			TransgenicTool singleTransgenicTool = validateRequiredEntity(transgenicToolDAO, "singleTransgenicTool", uiEntity.getSingleTransgenicTool(), dbEntity.getSingleTransgenicTool());
			dbEntity.setSingleTransgenicTool(singleTransgenicTool);
		}

		String componentSymbol = validateComponentSymbol(uiEntity);
		dbEntity.setComponentSymbol(componentSymbol);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.TRANSGENIC_TOOL_USE_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation(), dbEntity.getRelation());
		dbEntity.setRelation(relation);

		NCBITaxonTerm taxon = validateTaxon(uiEntity.getTaxon(), dbEntity.getTaxon());
		dbEntity.setTaxon(taxon);

		String taxonText = handleStringField(uiEntity.getTaxonText());
		dbEntity.setTaxonText(taxonText);

		List<Note> relatedNotes = validateRelatedNotes(uiEntity.getRelatedNotes(), VocabularyConstants.TRANSGENIC_TOOL_USE_NOTE_TYPES_VOCABULARY_TERM_SET);
		if (dbEntity.getRelatedNotes() != null) {
			dbEntity.getRelatedNotes().clear();
		}
		if (relatedNotes != null) {
			if (dbEntity.getRelatedNotes() == null) {
				dbEntity.setRelatedNotes(new ArrayList<>());
			}
			dbEntity.getRelatedNotes().addAll(relatedNotes);
		}

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorTitle);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}

	public String validateComponentSymbol(TransgenicToolUseSlotAnnotation uiEntity) {
		String field = "componentSymbol";
		if (StringUtils.isBlank(uiEntity.getComponentSymbol())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		return uiEntity.getComponentSymbol();
	}

}
