package org.alliancegenome.curation_api.services.validation.dto.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.associations.ConstructCassetteAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.ConstructCassetteAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.ConstructCassetteAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CassetteService;
import org.alliancegenome.curation_api.services.ConstructService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6535: a cassette that is part of a construct.
 *
 * Mirrors ConstructGenomicEntityAssociationDTOValidator, including its lookup-then-create shape:
 * an association matching subject, relation and object is reused rather than duplicated.
 */
@RequestScoped
public class ConstructCassetteAssociationDTOValidator extends EvidenceAssociationDTOValidator<ConstructCassetteAssociation, ConstructCassetteAssociationDTO> {

	@Inject ConstructService constructService;
	@Inject CassetteService cassetteService;
	@Inject ConstructCassetteAssociationDAO lConstructCassetteAssociationDAO;

	public ObjectResponse<ConstructCassetteAssociation> validateConstructCassetteAssociationDTO(ConstructCassetteAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<ConstructCassetteAssociation>();

		List<Long> subjectIds = null;
		if (StringUtils.isBlank(dto.getConstructIdentifier())) {
			response.addErrorMessage("construct_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectIds = constructService.findIdsByIdentifierString(dto.getConstructIdentifier());
			if (subjectIds == null || subjectIds.size() != 1) {
				response.addErrorMessage("construct_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getConstructIdentifier() + ")");
			}
		}

		List<Long> objectIds = null;
		if (StringUtils.isBlank(dto.getCassetteIdentifier())) {
			response.addErrorMessage("cassette_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = cassetteService.findIdsByIdentifierString(dto.getCassetteIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				response.addErrorMessage("cassette_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getCassetteIdentifier() + ")");
			}
		}

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation_name", dto.getRelationName(), VocabularyConstants.CONSTRUCT_CASSETTE_RELATION_VOCABULARY_TERM_SET);

		ConstructCassetteAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null && objectIds.size() == 1 && relation != null) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("constructAssociationSubject.id", subjectIds.get(0));
			params.put("relation.id", relation.getId());
			params.put("constructCassetteAssociationObject.id", objectIds.get(0));

			SearchResponse<ConstructCassetteAssociation> searchResponse = lConstructCassetteAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			} else {
				if (association == null) {
					association = new ConstructCassetteAssociation();
				}

				association.setRelation(relation);

				if (!StringUtils.isBlank(dto.getConstructIdentifier())) {
					Construct subject = constructService.findByIdentifierString(dto.getConstructIdentifier());
					if (subject == null) {
						response.addErrorMessage("construct_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getConstructIdentifier() + ")");
					} else if (dataProvider != null && !subject.getDataProvider().getAbbreviation().equals(dataProvider.sourceOrganization)) {
						response.addErrorMessage("construct_identifier", ValidationConstants.INVALID_MESSAGE + " for " + dataProvider.name() + " load (" + dto.getConstructIdentifier() + ")");
					} else {
						association.setConstructAssociationSubject(subject);
					}
				}

				if (!StringUtils.isBlank(dto.getCassetteIdentifier())) {
					Cassette object = cassetteService.findByIdentifierString(dto.getCassetteIdentifier());
					if (object == null) {
						response.addErrorMessage("cassette_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getCassetteIdentifier() + ")");
					} else {
						association.setConstructCassetteAssociationObject(object);
					}
				}

			}

			association = validateEvidenceAssociationDTO(association, dto);

			if (association.getRelatedNotes() != null) {
				association.getRelatedNotes().clear();
			}

			List<Note> validatedNotes = validateNotes(dto.getNoteDtos(), VocabularyConstants.CONSTRUCT_CASSETTE_ASSOCIATION_NOTE_TYPES_VOCABULARY_TERM_SET);
			if (CollectionUtils.isNotEmpty(validatedNotes)) {
				if (association.getRelatedNotes() == null) {
					association.setRelatedNotes(new ArrayList<>());
				}
				association.getRelatedNotes().addAll(validatedNotes);
			}
		}

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		response.setEntity(lConstructCassetteAssociationDAO.persist(association));

		return response;
	}
}
