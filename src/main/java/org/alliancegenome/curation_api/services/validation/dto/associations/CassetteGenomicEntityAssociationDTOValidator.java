package org.alliancegenome.curation_api.services.validation.dto.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.associations.CassetteGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.CassetteGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CassetteService;
import org.alliancegenome.curation_api.services.GenomicEntityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CassetteGenomicEntityAssociationDTOValidator extends EvidenceAssociationDTOValidator<CassetteGenomicEntityAssociation, CassetteGenomicEntityAssociationDTO> {

	@Inject
	CassetteService cassetteService;
	@Inject
	GenomicEntityService genomicEntityService;
	@Inject
	CassetteGenomicEntityAssociationDAO cassetteGenomicEntityAssociationDAO;

	public ObjectResponse<CassetteGenomicEntityAssociation> validateCassetteGenomicEntityAssociationDTO(CassetteGenomicEntityAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<CassetteGenomicEntityAssociation>();

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
		if (StringUtils.isBlank(dto.getGenomicEntityIdentifier())) {
			response.addErrorMessage("genomic_entity_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = genomicEntityService.findIdsByIdentifierString(dto.getGenomicEntityIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				response.addErrorMessage("genomic_entity_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGenomicEntityIdentifier() + ")");
			}
		}

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation_name", dto.getRelationName(), VocabularyConstants.CASSETTE_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET);

		CassetteGenomicEntityAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null && objectIds.size() == 1 && relation != null) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("cassetteAssociationSubject.id", subjectIds.get(0));
			params.put("relation.id", relation.getId());
			params.put("cassetteGenomicEntityAssociationObject.id", objectIds.get(0));

			SearchResponse<CassetteGenomicEntityAssociation> searchResponse = cassetteGenomicEntityAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			} else {
				if (association == null) {
					association = new CassetteGenomicEntityAssociation();
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

				if (!StringUtils.isBlank(dto.getGenomicEntityIdentifier())) {

					GenomicEntity object = genomicEntityService.findByIdentifierString(dto.getGenomicEntityIdentifier());
					if (object == null) {
						response.addErrorMessage("genomic_entity_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGenomicEntityIdentifier() + ")");
					} else {
						association.setCassetteGenomicEntityAssociationObject(object);
					}
				}

			}

			association = validateEvidenceAssociationDTO(association, dto);

			if (association.getRelatedNotes() != null) {
				association.getRelatedNotes().clear();
			}

			List<Note> validatedNotes = validateNotes(dto.getNoteDtos(), VocabularyConstants.CASSETTE_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
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

		response.setEntity(cassetteGenomicEntityAssociationDAO.persist(association));

		return response;
	}
}
