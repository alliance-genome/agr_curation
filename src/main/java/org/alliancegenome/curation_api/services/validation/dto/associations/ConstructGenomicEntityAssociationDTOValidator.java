package org.alliancegenome.curation_api.services.validation.dto.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.associations.ConstructGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.ConstructGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ConstructService;
import org.alliancegenome.curation_api.services.GenomicEntityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructGenomicEntityAssociationDTOValidator extends EvidenceAssociationDTOValidator<ConstructGenomicEntityAssociation, ConstructGenomicEntityAssociationDTO> {

	@Inject
	ConstructService constructService;
	@Inject
	GenomicEntityService genomicEntityService;
	@Inject
	ConstructGenomicEntityAssociationDAO constructGenomicEntityAssociationDAO;

	public ConstructGenomicEntityAssociation validateConstructGenomicEntityAssociationDTO(ConstructGenomicEntityAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<ConstructGenomicEntityAssociation>();

		List<Long> subjectIds = null;
		if (StringUtils.isBlank(dto.getConstructIdentifier())) {
			response.addErrorMessage("construct_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectIds = constructService.findIdsByIdentifierString(dto.getConstructIdentifier());
			if (subjectIds == null || subjectIds.size() != 1) {
				response.addErrorMessage("construct_identifier", ValidationConstants.INVALID_MESSAGE + " for " + dataProvider.name() + " load");
			}
		}
		
		List<Long> objectIds = null;
		if (StringUtils.isBlank(dto.getGenomicEntityIdentifier())) {
			response.addErrorMessage("genomic_entity_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = genomicEntityService.findIdsByIdentifierString(dto.getGenomicEntityIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				response.addErrorMessage("genomic_entity_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGenomicEntityIdentifier() + ")");
			}
		}
		
		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("genomic_entity_relation_name", dto.getGenomicEntityRelationName(), VocabularyConstants.CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET);

		ConstructGenomicEntityAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null && objectIds.size() == 1 && relation != null) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("constructAssociationSubject.id", subjectIds.get(0));
			params.put("relation.id", relation.getId());
			params.put("constructGenomicEntityAssociationObject.id", objectIds.get(0));

			SearchResponse<ConstructGenomicEntityAssociation> searchResponse = constructGenomicEntityAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			} else {
				if (association == null) {
					association = new ConstructGenomicEntityAssociation();
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
				
				if (!StringUtils.isBlank(dto.getGenomicEntityIdentifier())) {

					GenomicEntity object = genomicEntityService.findByIdentifierString(dto.getGenomicEntityIdentifier());
					if (object == null) {
						response.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGenomicEntityIdentifier() + ")");
					} else if (dataProvider != null && !object.getDataProvider().getAbbreviation().equals(dataProvider.sourceOrganization)) {
						response.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " for " + dataProvider.name() + " load (" + dto.getGenomicEntityIdentifier() + ")");
					} else {
						association.setConstructGenomicEntityAssociationObject(object);
					}
				}
				
			}
			
			association = validateEvidenceAssociationDTO(association, dto);
			
			if (association.getRelatedNotes() != null) {
				association.getRelatedNotes().clear();
			}

			List<Note> validatedNotes = validateNotes(dto.getNoteDtos(), VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
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

		return constructGenomicEntityAssociationDAO.persist(association);
	}
}
