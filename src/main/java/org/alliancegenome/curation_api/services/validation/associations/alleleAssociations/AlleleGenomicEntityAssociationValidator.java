package org.alliancegenome.curation_api.services.validation.associations.alleleAssociations;

import java.util.Objects;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.NoteValidator;
import org.alliancegenome.curation_api.services.validation.associations.EvidenceAssociationValidator;

import jakarta.inject.Inject;

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

		if (uiEntity.getRelatedNote() == null)
			return null;
			
		ObjectResponse<Note> noteResponse = noteValidator.validateNote(uiEntity.getRelatedNote(), VocabularyConstants.ALLELE_GENOMIC_ENTITY_ASSOCIATION_NOTE_TYPES_VOCABULARY_TERM_SET);
		if (noteResponse.getEntity() == null) {
			addMessageResponse(field, noteResponse.errorMessagesString());
			return null;
		}
		return noteResponse.getEntity();
	}

	public AlleleGenomicEntityAssociation validateAlleleGenomicEntityAssociationFields(AlleleGenomicEntityAssociation uiEntity, AlleleGenomicEntityAssociation dbEntity) {
		
		dbEntity = (AlleleGenomicEntityAssociation) validateEvidenceAssociationFields(uiEntity, dbEntity);

		ECOTerm evidenceCode = validateEvidenceCode(uiEntity, dbEntity);
		dbEntity.setEvidenceCode(evidenceCode);

		Note relatedNote = validateRelatedNote(uiEntity, dbEntity);
		dbEntity.setRelatedNote(relatedNote);

		return dbEntity;
	}
}
