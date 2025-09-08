package org.alliancegenome.curation_api.services.validation.dto.base;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.SubmittedObjectDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.helpers.UniqueIdentifierHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import jakarta.inject.Inject;

public class SubmittedObjectDTOValidator<E extends SubmittedObject, D extends SubmittedObjectDTO> extends AuditedObjectDTOValidator<E, D> {

	@Inject CrossReferenceDAO crossReferenceDAO;

	public E validateSubmittedObjectDTO(E entity, D dto, String noteTypeVocabularyTermSet) {

		entity = validateAuditedObjectDTO(entity, dto);
		
		UniqueIdentifierHelper.setSubmittedObjectIdentifiers(dto, entity, null);
		
		if (dto.getDataProviderDto() == null) {
			response.addErrorMessage("data_provider_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<ImmutablePair<Organization, CrossReference>> dpResponse = validateDataProviderDTO(dto.getDataProviderDto(), entity.getDataProviderCrossReference());
			if (dpResponse.hasErrors()) {
				response.addErrorMessage("data_provider_dto", dpResponse.errorMessagesString());
			} else {
				entity.setDataProvider(dpResponse.getEntity().getLeft());
				if (dpResponse.getEntity().getRight() != null) {
					entity.setDataProviderCrossReference(crossReferenceDAO.persist(dpResponse.getEntity().getRight()));
				} else {
					entity.setDataProviderCrossReference(null);
				}
			}
		}
		
		if (entity.getRelatedNotes() != null) {
			entity.getRelatedNotes().clear();
		}
		
		List<Note> relatedNotes = validateRelatedNotes(entity, dto, noteTypeVocabularyTermSet);
		if (relatedNotes != null) {
			if (entity.getRelatedNotes() == null) {
				entity.setRelatedNotes(new ArrayList<>());
			}
			entity.getRelatedNotes().addAll(relatedNotes);
		}

		return entity;
	}
	


	private List<Note> validateRelatedNotes(E entity, D dto, String noteTypeVocabularyTermSet) {
		String field = "relatedNotes";

		if (entity.getRelatedNotes() != null) {
			entity.getRelatedNotes().clear();
		}

		List<Note> validatedNotes = new ArrayList<Note>();
		List<String> noteIdentities = new ArrayList<String>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getNoteDtos())) {
			for (int ix = 0; ix < dto.getNoteDtos().size(); ix++) {
				ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(dto.getNoteDtos().get(ix), noteTypeVocabularyTermSet);
				if (noteResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, noteResponse.getErrorMessages());
				} else {
					String noteIdentity = NoteIdentityHelper.noteDtoIdentity(dto.getNoteDtos().get(ix));
					if (!noteIdentities.contains(noteIdentity)) {
						noteIdentities.add(noteIdentity);
						validatedNotes.add(noteResponse.getEntity());
					}
				}
			}
		}

		if (!allValid) {
			response.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedNotes)) {
			return null;
		}

		return validatedNotes;
	}

}
