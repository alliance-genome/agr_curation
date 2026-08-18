package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.CassetteComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteComponentSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CassetteComponentSlotAnnotationValidator extends SlotAnnotationValidator<CassetteComponentSlotAnnotation> {

	@Inject CassetteComponentSlotAnnotationDAO cassetteComponentDAO;
	@Inject CassetteDAO cassetteDAO;

	public ObjectResponse<CassetteComponentSlotAnnotation> validateCassetteComponentSlotAnnotation(CassetteComponentSlotAnnotation uiEntity) {
		CassetteComponentSlotAnnotation component = validateCassetteComponentSlotAnnotation(uiEntity, false, false);
		response.setEntity(component);
		return response;
	}

	public CassetteComponentSlotAnnotation validateCassetteComponentSlotAnnotation(CassetteComponentSlotAnnotation uiEntity, Boolean throwError, Boolean validateCassette) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update CassetteComponentSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		CassetteComponentSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = cassetteComponentDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find CassetteComponentSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new CassetteComponentSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (CassetteComponentSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateCassette) {
			Cassette singleCassette = validateRequiredEntity(cassetteDAO, "singleCassette", uiEntity.getSingleCassette(), dbEntity.getSingleCassette());
			dbEntity.setSingleCassette(singleCassette);
		}

		String componentSymbol = validateComponentSymbol(uiEntity);
		dbEntity.setComponentSymbol(componentSymbol);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.CASSETTE_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation(), dbEntity.getRelation());
		dbEntity.setRelation(relation);

		NCBITaxonTerm taxon = validateTaxon(uiEntity.getTaxon(), dbEntity.getTaxon());
		dbEntity.setTaxon(taxon);

		String taxonText = handleStringField(uiEntity.getTaxonText());
		dbEntity.setTaxonText(taxonText);

		List<Note> relatedNotes = validateRelatedNotes(uiEntity.getRelatedNotes(), VocabularyConstants.CASSETTE_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
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

	public String validateComponentSymbol(CassetteComponentSlotAnnotation uiEntity) {
		String field = "componentSymbol";
		if (StringUtils.isBlank(uiEntity.getComponentSymbol())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		return uiEntity.getComponentSymbol();
	}

}
