package org.alliancegenome.curation_api.services.validation.associations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.dao.associations.TransgenicToolTransgenicToolAssociationDAO;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6655: one tool - tool association as the transgenic tool detail page submits it. The
 * association carries no evidence or notes (it extends Association), so only its relation, the
 * other tool and the audited fields are validated. Its subject is the tool whose associations are
 * being replaced, which TransgenicToolValidator assigns.
 */
@RequestScoped
public class TransgenicToolTransgenicToolAssociationValidator extends AuditedObjectValidator<TransgenicToolTransgenicToolAssociation> {

	@Inject TransgenicToolDAO transgenicToolDAO;
	@Inject TransgenicToolTransgenicToolAssociationDAO transgenicToolTransgenicToolAssociationDAO;

	/**
	 * @param uiEntity the submitted association; one with an id must be stored
	 * @return a response holding the validated association, or no entity and the field errors
	 */
	public ObjectResponse<TransgenicToolTransgenicToolAssociation> validateTransgenicToolTransgenicToolAssociation(TransgenicToolTransgenicToolAssociation uiEntity) {
		response = new ObjectResponse<>(uiEntity);

		Long id = uiEntity.getId();
		TransgenicToolTransgenicToolAssociation dbEntity = null;
		if (id != null) {
			dbEntity = transgenicToolTransgenicToolAssociationDAO.find(id);
			if (dbEntity == null) {
				addMessageResponse("id", "Could not find TransgenicToolTransgenicToolAssociation with ID: [" + id + "]");
				response.setEntity(null);
				return response;
			}
		} else {
			dbEntity = new TransgenicToolTransgenicToolAssociation();
		}

		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity, id == null);

		TransgenicTool object = validateRequiredEntity(transgenicToolDAO, "transgenicToolTransgenicToolAssociationObject", uiEntity.getTransgenicToolTransgenicToolAssociationObject(), dbEntity.getTransgenicToolTransgenicToolAssociationObject());
		dbEntity.setTransgenicToolTransgenicToolAssociationObject(object);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.TRANSGENIC_TOOL_ASSOCIATION_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation(), dbEntity.getRelation());
		dbEntity.setRelation(relation);

		response.setEntity(response.hasErrors() ? null : dbEntity);
		return response;
	}
}
