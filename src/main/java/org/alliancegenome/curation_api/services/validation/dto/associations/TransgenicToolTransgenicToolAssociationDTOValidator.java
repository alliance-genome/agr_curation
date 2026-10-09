package org.alliancegenome.curation_api.services.validation.dto.associations;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.dao.associations.TransgenicToolTransgenicToolAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.TransgenicToolTransgenicToolAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.TransgenicToolService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6543: two compatible transgenic tools. Mirrors AgmAgmAssociationDTOValidator, the other
 * association between two entities of one type: an association matching subject, relation and object
 * is reused rather than duplicated, and both tools must belong to the provider whose load this is.
 */
@RequestScoped
public class TransgenicToolTransgenicToolAssociationDTOValidator extends AuditedObjectDTOValidator<TransgenicToolTransgenicToolAssociation, TransgenicToolTransgenicToolAssociationDTO> {

	@Inject TransgenicToolService transgenicToolService;
	@Inject TransgenicToolDAO transgenicToolDAO;
	@Inject TransgenicToolTransgenicToolAssociationDAO transgenicToolTransgenicToolAssociationDAO;

	public ObjectResponse<TransgenicToolTransgenicToolAssociation> validateTransgenicToolTransgenicToolAssociationDTO(TransgenicToolTransgenicToolAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<TransgenicToolTransgenicToolAssociation>();

		List<Long> subjectIds = null;
		if (StringUtils.isBlank(dto.getTransgenicToolSubjectIdentifier())) {
			response.addErrorMessage("transgenic_tool_subject_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectIds = transgenicToolService.findIdsByIdentifierString(dto.getTransgenicToolSubjectIdentifier());
			if (subjectIds == null || subjectIds.size() != 1) {
				response.addErrorMessage("transgenic_tool_subject_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTransgenicToolSubjectIdentifier() + ")");
			}
		}

		List<Long> objectIds = null;
		if (StringUtils.isBlank(dto.getTransgenicToolObjectIdentifier())) {
			response.addErrorMessage("transgenic_tool_object_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = transgenicToolService.findIdsByIdentifierString(dto.getTransgenicToolObjectIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				response.addErrorMessage("transgenic_tool_object_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTransgenicToolObjectIdentifier() + ")");
			}
		}

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation_name", dto.getRelationName(), VocabularyConstants.TRANSGENIC_TOOL_ASSOCIATION_RELATION_VOCABULARY_TERM_SET);

		TransgenicToolTransgenicToolAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null && objectIds.size() == 1 && relation != null) {
			HashMap<String, Object> params = new HashMap<>();
			params.put(EntityFieldConstants.TRANSGENIC_TOOL_ASSOCIATION_SUBJECT + ".id", subjectIds.get(0));
			params.put(EntityFieldConstants.RELATION + ".id", relation.getId());
			params.put("transgenicToolTransgenicToolAssociationObject.id", objectIds.get(0));

			SearchResponse<TransgenicToolTransgenicToolAssociation> searchResponse = transgenicToolTransgenicToolAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			}
		}

		if (association == null) {
			association = new TransgenicToolTransgenicToolAssociation();
		}

		association.setRelation(relation);

		if (association.getTransgenicToolAssociationSubject() == null && subjectIds != null && subjectIds.size() == 1) {
			TransgenicTool subject = validateTransgenicTool("transgenic_tool_subject_identifier", dto.getTransgenicToolSubjectIdentifier(), subjectIds.get(0), dataProvider);
			association.setTransgenicToolAssociationSubject(subject);
		}

		if (association.getTransgenicToolTransgenicToolAssociationObject() == null && objectIds != null && objectIds.size() == 1) {
			TransgenicTool object = validateTransgenicTool("transgenic_tool_object_identifier", dto.getTransgenicToolObjectIdentifier(), objectIds.get(0), dataProvider);
			association.setTransgenicToolTransgenicToolAssociationObject(object);
		}

		association = validateAuditedObjectDTO(association, dto);

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		response.setEntity(transgenicToolTransgenicToolAssociationDAO.persist(association));

		return response;
	}

	/**
	 * The tool with the id already resolved from {@code identifier}, or null with an error when it is
	 * unknown or another provider's. Fetched by id rather than looked up by identifier a second time.
	 */
	private TransgenicTool validateTransgenicTool(String field, String identifier, Long id, BackendBulkDataProvider dataProvider) {
		TransgenicTool tool = transgenicToolDAO.find(id);
		if (tool == null) {
			response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE + " (" + identifier + ")");
			return null;
		}
		if (dataProvider != null && !tool.getDataProvider().getAbbreviation().equals(dataProvider.sourceOrganization)) {
			response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE + " for " + dataProvider.name() + " load (" + identifier + ")");
			return null;
		}
		return tool;
	}
}
