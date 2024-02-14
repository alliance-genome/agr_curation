package org.alliancegenome.curation_api.services.validation.associations.constructAssociations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.dao.associations.constructAssociations.ConstructGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.validation.NoteValidator;
import org.alliancegenome.curation_api.services.validation.associations.EvidenceAssociationValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructGenomicEntityAssociationValidator extends EvidenceAssociationValidator<ConstructGenomicEntityAssociation> {

	@Inject
	ConstructDAO constructDAO;
	@Inject
	GenomicEntityDAO genomicEntityDAO;
	@Inject
	ConstructGenomicEntityAssociationDAO constructGenomicEntityAssociationDAO;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	NoteValidator noteValidator;

	private String errorMessage;

	public ObjectResponse<ConstructGenomicEntityAssociation> validateConstructGenomicEntityAssociation(ConstructGenomicEntityAssociation uiEntity) {
		ConstructGenomicEntityAssociation geAssociation = validateConstructGenomicEntityAssociation(uiEntity, false, false);
		response.setEntity(geAssociation);
		return response;
	}
	
	public ConstructGenomicEntityAssociation validateConstructGenomicEntityAssociation(ConstructGenomicEntityAssociation uiEntity, Boolean throwError, Boolean validateConstruct) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create/update Construct GenomicEntity Association: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		ConstructGenomicEntityAssociation dbEntity = null;
		if (id != null) {
			dbEntity = constructGenomicEntityAssociationDAO.find(id);
			if (dbEntity == null) {
				addMessageResponse("Could not find ConstructGenomicEntityAssociation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new ConstructGenomicEntityAssociation();
		}
		
		dbEntity = (ConstructGenomicEntityAssociation) validateEvidenceAssociationFields(uiEntity, dbEntity);

		if (validateConstruct) {
			Construct subject = validateSubjectReagent(uiEntity, dbEntity);
			dbEntity.setSubjectReagent(subject);
		}
		
		GenomicEntity object = validateObject(uiEntity, dbEntity);
		dbEntity.setObjectBiologicalEntity(object);

		VocabularyTerm relation = validateRelation(uiEntity, dbEntity);
		dbEntity.setRelation(relation);
		
		List<Note> relatedNotes = validateRelatedNotes(uiEntity, dbEntity);
		if (dbEntity.getRelatedNotes() != null)
			dbEntity.getRelatedNotes().clear();
		if (relatedNotes != null) {
			if (dbEntity.getRelatedNotes() == null)
				dbEntity.setRelatedNotes(new ArrayList<>());
			dbEntity.getRelatedNotes().addAll(relatedNotes);
		}

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorMessage);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}
	
	private Construct validateSubjectReagent(ConstructGenomicEntityAssociation uiEntity, ConstructGenomicEntityAssociation dbEntity) {
		String field = "subjectReagent";
		if (ObjectUtils.isEmpty(uiEntity.getSubjectReagent())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		Construct subjectEntity = null;
		if (uiEntity.getSubjectReagent().getId() != null)
			subjectEntity = constructDAO.find(uiEntity.getSubjectReagent().getId());
		if (subjectEntity == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (subjectEntity.getObsolete() && (dbEntity.getSubjectReagent() == null || !subjectEntity.getId().equals(dbEntity.getSubjectReagent().getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return subjectEntity;

	}

	private GenomicEntity validateObject(ConstructGenomicEntityAssociation uiEntity, ConstructGenomicEntityAssociation dbEntity) {
		if (ObjectUtils.isEmpty(uiEntity.getObjectBiologicalEntity())) {
			addMessageResponse("objectBiologicalEntity", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		GenomicEntity objectEntity = null;
		if (uiEntity.getObjectBiologicalEntity().getId() != null)
			objectEntity = genomicEntityDAO.find(uiEntity.getObjectBiologicalEntity().getId());
		if (objectEntity == null) {
			addMessageResponse("objectBiologicalEntity", ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (objectEntity.getObsolete() && (dbEntity.getObjectBiologicalEntity() == null || !objectEntity.getId().equals(dbEntity.getObjectBiologicalEntity().getId()))) {
			addMessageResponse("objectBiologicalEntity", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return objectEntity;

	}

	private VocabularyTerm validateRelation(ConstructGenomicEntityAssociation uiEntity, ConstructGenomicEntityAssociation dbEntity) {
		String field = "relation";
		if (uiEntity.getRelation() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm relation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation().getName()).getEntity();

		if (relation == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (relation.getObsolete() && (dbEntity.getRelation() == null || !relation.getName().equals(dbEntity.getRelation().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return relation;
	}
	
	public List<Note> validateRelatedNotes(ConstructGenomicEntityAssociation uiEntity, ConstructGenomicEntityAssociation dbEntity) {
		String field = "relatedNotes";

		List<Note> validatedNotes = new ArrayList<Note>();
		Set<String> validatedNoteIdentities = new HashSet<>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getRelatedNotes())) {
			for (int ix = 0; ix < uiEntity.getRelatedNotes().size(); ix++) {
				Note note = uiEntity.getRelatedNotes().get(ix);
				ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
				if (noteResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, noteResponse.getErrorMessages());
				} else {
					note = noteResponse.getEntity();
				
					String noteIdentity = NoteIdentityHelper.noteIdentity(note);
					if (validatedNoteIdentities.contains(noteIdentity)) {
						allValid = false;
						Map<String, String> duplicateError = new HashMap<>();
						duplicateError.put("freeText", ValidationConstants.DUPLICATE_MESSAGE + " (" + noteIdentity + ")");
						response.addErrorMessages(field, ix, duplicateError);
					} else {
						validatedNoteIdentities.add(noteIdentity);
						validatedNotes.add(note);
					}
				}
			}
		}
		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedNotes))
			return null;

		return validatedNotes;
	}
}
