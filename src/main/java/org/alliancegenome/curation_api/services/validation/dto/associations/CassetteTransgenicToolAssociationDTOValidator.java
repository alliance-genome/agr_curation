package org.alliancegenome.curation_api.services.validation.dto.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.associations.CassetteTransgenicToolAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.CassetteTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteTransgenicToolAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CassetteService;
import org.alliancegenome.curation_api.services.TransgenicToolService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6535: a cassette component that is a transgenic tool.
 *
 * Mirrors ConstructGenomicEntityAssociationDTOValidator, including its lookup-then-create shape:
 * an association matching subject, relation and object is reused rather than duplicated.
 */
@RequestScoped
public class CassetteTransgenicToolAssociationDTOValidator extends EvidenceAssociationDTOValidator<CassetteTransgenicToolAssociation, CassetteTransgenicToolAssociationDTO> {

	@Inject CassetteService cassetteService;
	@Inject TransgenicToolService transgenicToolService;
	@Inject CassetteTransgenicToolAssociationDAO lCassetteTransgenicToolAssociationDAO;

	public ObjectResponse<CassetteTransgenicToolAssociation> validateCassetteTransgenicToolAssociationDTO(CassetteTransgenicToolAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<CassetteTransgenicToolAssociation>();

		List<Long> subjectIds = null;
		if (StringUtils.isBlank(dto.getCassetteIdentifier())) {
			response.addErrorMessage("cassette_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectIds = cassetteService.findIdsByIdentifierString(dto.getCassetteIdentifier());
			if (subjectIds == null || subjectIds.size() != 1) {
				response.addErrorMessage("cassette_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getCassetteIdentifier() + ")");
			}
		}

		List<Long> objectIds = null;
		if (StringUtils.isBlank(dto.getTransgenicToolIdentifier())) {
			response.addErrorMessage("transgenic_tool_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = transgenicToolService.findIdsByIdentifierString(dto.getTransgenicToolIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				response.addErrorMessage("transgenic_tool_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTransgenicToolIdentifier() + ")");
			}
		}

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation_name", dto.getRelationName(), VocabularyConstants.CASSETTE_TRANSGENIC_TOOL_RELATION_VOCABULARY_TERM_SET);

		CassetteTransgenicToolAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null && objectIds.size() == 1 && relation != null) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("cassetteAssociationSubject.id", subjectIds.get(0));
			params.put("relation.id", relation.getId());
			params.put("cassetteTransgenicToolAssociationObject.id", objectIds.get(0));

			SearchResponse<CassetteTransgenicToolAssociation> searchResponse = lCassetteTransgenicToolAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			} else {
				if (association == null) {
					association = new CassetteTransgenicToolAssociation();
				}

				association.setRelation(relation);

				if (!StringUtils.isBlank(dto.getCassetteIdentifier())) {
					Cassette subject = cassetteService.findByIdentifierString(dto.getCassetteIdentifier());
					if (subject == null) {
						response.addErrorMessage("cassette_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getCassetteIdentifier() + ")");
					} else if (dataProvider != null && !subject.getDataProvider().getAbbreviation().equals(dataProvider.sourceOrganization)) {
						response.addErrorMessage("cassette_identifier", ValidationConstants.INVALID_MESSAGE + " for " + dataProvider.name() + " load (" + dto.getCassetteIdentifier() + ")");
					} else {
						association.setCassetteAssociationSubject(subject);
					}
				}

				if (!StringUtils.isBlank(dto.getTransgenicToolIdentifier())) {
					TransgenicTool object = transgenicToolService.findByIdentifierString(dto.getTransgenicToolIdentifier());
					if (object == null) {
						response.addErrorMessage("transgenic_tool_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTransgenicToolIdentifier() + ")");
					} else {
						association.setCassetteTransgenicToolAssociationObject(object);
					}
				}

			}

			association = validateEvidenceAssociationDTO(association, dto);

			if (association.getRelatedNotes() != null) {
				association.getRelatedNotes().clear();
			}

			List<Note> validatedNotes = validateNotes(dto.getNoteDtos(), VocabularyConstants.CASSETTE_TRANSGENIC_TOOL_ASSOCIATION_NOTE_TYPES_VOCABULARY_TERM_SET);
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

		response.setEntity(lCassetteTransgenicToolAssociationDAO.persist(association));

		return response;
	}
}
