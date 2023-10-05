package org.alliancegenome.curation_api.services.validation.associations.alleleAssociations;

import java.util.Objects;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.NoteValidator;
import org.alliancegenome.curation_api.services.validation.associations.EvidenceAssociationValidator;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class AlleleGenomicEntityAssociationValidator extends EvidenceAssociationValidator {

	@Inject
	NoteValidator noteValidator;
	@Inject
	NoteDAO noteDAO;
	@Inject
	EcoTermDAO ecoTermDAO;
	@Inject
	AlleleDAO alleleDAO;
	
	public ECOTerm validateEvidenceCode(AlleleGenomicEntityAssociation uiEntity, AlleleGenomicEntityAssociation dbEntity) {
		String field = "evidenceCode";
		if (uiEntity.getEvidenceCode() == null)
			return null;
		
		ECOTerm evidenceCode = ecoTermDAO.find(uiEntity.getEvidenceCode().getCurie());
		if (evidenceCode == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		if (evidenceCode.getObsolete() && (dbEntity.getEvidenceCode() == null || !dbEntity.getEvidenceCode().getCurie().equals(evidenceCode.getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		if (!evidenceCode.getSubsets().contains(OntologyConstants.AGR_ECO_TERM_SUBSET)) {
			addMessageResponse(field, ValidationConstants.UNSUPPORTED_MESSAGE);
			return null;
		}
		
		return evidenceCode;
	}
		
	public Note validateRelatedNote(AlleleGenomicEntityAssociation uiEntity, AlleleGenomicEntityAssociation dbEntity) {
		String field = "relatedNote";

		Note note = null;
		if (uiEntity.getRelatedNote() != null) {
			ObjectResponse<Note> noteResponse = noteValidator.validateNote(uiEntity.getRelatedNote(), VocabularyConstants.ALLELE_GENOMIC_ENTITY_ASSOCIATION_NOTE_TYPES_VOCABULARY_TERM_SET);
			if (noteResponse.getEntity() == null) {
				addMessageResponse(field, noteResponse.errorMessagesString());
				return null;
			}
			note = noteResponse.getEntity();
			if (note.getId() == null)
				note = noteDAO.persist(note);
		}

		Long previousNoteId = null;
		if (dbEntity.getRelatedNote() != null)
			previousNoteId = dbEntity.getRelatedNote().getId();
		
		dbEntity.setRelatedNote(null);
		if (previousNoteId != null && !Objects.equals(note.getId(), previousNoteId))
			noteDAO.remove(previousNoteId);
			
		return note;
	}
	
	private Allele validateSubject(AlleleGenomicEntityAssociation uiEntity, AlleleGenomicEntityAssociation dbEntity) {
		String field = "subject";
		if (ObjectUtils.isEmpty(uiEntity.getSubject()) || StringUtils.isBlank(uiEntity.getSubject().getCurie())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		Allele subjectEntity = alleleDAO.find(uiEntity.getSubject().getCurie());
		if (subjectEntity == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (subjectEntity.getObsolete() && (dbEntity.getSubject() == null || !subjectEntity.getCurie().equals(dbEntity.getSubject().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return subjectEntity;

	}

	public AlleleGenomicEntityAssociation validateAlleleGenomicEntityAssociationFields(AlleleGenomicEntityAssociation uiEntity, AlleleGenomicEntityAssociation dbEntity, Boolean validateAllele) {
		
		dbEntity = (AlleleGenomicEntityAssociation) validateEvidenceAssociationFields(uiEntity, dbEntity);

		if (validateAllele) {
			Allele subject = validateSubject(uiEntity, dbEntity);
			dbEntity.setSubject(subject);
		}
		
		ECOTerm evidenceCode = validateEvidenceCode(uiEntity, dbEntity);
		dbEntity.setEvidenceCode(evidenceCode);

		Note relatedNote = validateRelatedNote(uiEntity, dbEntity);
		dbEntity.setRelatedNote(relatedNote);

		return dbEntity;
	}
}
