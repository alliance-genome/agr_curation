package org.alliancegenome.curation_api.services.validation.dto.associations.constructAssociations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.associations.constructAssociations.ConstructGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.constructAssociations.ConstructGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.validation.dto.NoteDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.associations.EvidenceAssociationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructGenomicEntityAssociationDTOValidator extends EvidenceAssociationDTOValidator {

	@Inject
	ConstructDAO constructDAO;
	@Inject
	GenomicEntityDAO genomicEntityDAO;
	@Inject
	NoteDTOValidator noteDtoValidator;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	ConstructGenomicEntityAssociationDAO constructGenomicEntityAssociationDAO;

	private ObjectResponse<ConstructGenomicEntityAssociation> assocResponse = new ObjectResponse<ConstructGenomicEntityAssociation>();
	
	public ConstructGenomicEntityAssociation validateConstructGenomicEntityAssociationDTO(ConstructGenomicEntityAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ObjectValidationException {
		
		Construct construct = null;
		if (StringUtils.isNotBlank(dto.getConstructIdentifier())) {
			SearchResponse<Construct> res = constructDAO.findByField("modEntityId", dto.getConstructIdentifier());
			if (res == null || res.getSingleResult() == null) {
				res = constructDAO.findByField("modInternalId", dto.getConstructIdentifier());
			}
			if (res == null || res.getSingleResult() == null) {
				assocResponse.addErrorMessage("construct_identifier", ValidationConstants.INVALID_MESSAGE);
			} else {
				construct = res.getSingleResult();
				if (beDataProvider != null && !construct.getDataProvider().getSourceOrganization().getAbbreviation().equals(beDataProvider.sourceOrganization))
					assocResponse.addErrorMessage("construct_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load");
			}
		} else {
			assocResponse.addErrorMessage("construct_identifier", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		GenomicEntity genomicEntity = null;
		if (StringUtils.isBlank(dto.getGenomicEntityIdentifier())) {
			assocResponse.addErrorMessage("genomic_entity_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			genomicEntity = genomicEntityDAO.findByIdentifierString(dto.getGenomicEntityIdentifier());
			if (genomicEntity == null)
				assocResponse.addErrorMessage("genomic_entity_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGenomicEntityIdentifier() + ")");
		}
		
		ConstructGenomicEntityAssociation association = null;
		if (construct != null && StringUtils.isNotBlank(dto.getGenomicEntityRelationName()) && genomicEntity != null) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("subject.id", construct.getId());
			params.put("relation.name", dto.getGenomicEntityRelationName());
			params.put("object.id", genomicEntity.getId());
			
			SearchResponse<ConstructGenomicEntityAssociation> searchResponse = constructGenomicEntityAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			}
		}
		if (association == null)
			association = new ConstructGenomicEntityAssociation();
		
		association.setSubject(construct);
		association.setObject(genomicEntity);
		
		ObjectResponse<ConstructGenomicEntityAssociation> eviResponse = validateEvidenceAssociationDTO(association, dto);
		assocResponse.addErrorMessages(eviResponse.getErrorMessages());
		association = eviResponse.getEntity();
		
		if (StringUtils.isNotEmpty(dto.getGenomicEntityRelationName())) {
			VocabularyTerm relation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET, dto.getGenomicEntityRelationName()).getEntity();
			if (relation == null)
				assocResponse.addErrorMessage("genomic_entity_relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGenomicEntityRelationName() + ")");
			association.setRelation(relation);
		} else {
			assocResponse.addErrorMessage("genomic_entity_relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		List<Note> relatedNotes = validateRelatedNotes(association, dto);
		if (relatedNotes != null) {
			if (association.getRelatedNotes() == null)
				association.setRelatedNotes(new ArrayList<>());
			association.getRelatedNotes().addAll(relatedNotes);
		}
		
		if (assocResponse.hasErrors())
			throw new ObjectValidationException(dto, assocResponse.errorMessagesString());

		association = constructGenomicEntityAssociationDAO.persist(association);
		
		return association;
	}
	
	private List<Note> validateRelatedNotes(ConstructGenomicEntityAssociation association, ConstructGenomicEntityAssociationDTO dto) {
		String field = "relatedNotes";
	
		if (association.getRelatedNotes() != null)
			association.getRelatedNotes().clear();
		
		List<Note> validatedNotes = new ArrayList<Note>();
		List<String> noteIdentities = new ArrayList<String>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getNoteDtos())) {
			for (int ix = 0; ix < dto.getNoteDtos().size(); ix++) {
				ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(dto.getNoteDtos().get(ix), VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
				if (noteResponse.hasErrors()) {
					allValid = false;
					assocResponse.addErrorMessages(field, ix, noteResponse.getErrorMessages());
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
			assocResponse.convertMapToErrorMessages(field);
			return null;
		}
		
		if (CollectionUtils.isEmpty(validatedNotes))
			return null;
		
		return validatedNotes;
	}
}
