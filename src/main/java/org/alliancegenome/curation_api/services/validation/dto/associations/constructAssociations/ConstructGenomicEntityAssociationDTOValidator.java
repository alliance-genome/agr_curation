package org.alliancegenome.curation_api.services.validation.dto.associations.constructAssociations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.associations.constructAssociations.ConstructGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
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

import lombok.extern.jbosslog.JBossLog;
@JBossLog
@RequestScoped
public class ConstructGenomicEntityAssociationDTOValidator extends EvidenceAssociationDTOValidator {

	@Inject
	ConstructDAO constructDAO;
	@Inject
	GenomicEntityDAO genomicEntityDAO;
	@Inject
	NoteDAO noteDAO;
	@Inject
	NoteDTOValidator noteDtoValidator;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	ConstructGenomicEntityAssociationDAO constructGenomicEntityAssociationDAO;

	public ConstructGenomicEntityAssociation validateConstructGenomicEntityAssociationDTO(ConstructGenomicEntityAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ObjectValidationException {
		ObjectResponse<ConstructGenomicEntityAssociation> assocResponse = new ObjectResponse<ConstructGenomicEntityAssociation>();

		Construct construct = null;
		if (StringUtils.isNotBlank(dto.getConstructIdentifier())) {
			SearchResponse<Construct> res = constructDAO.findByField("modEntityId", dto.getConstructIdentifier());
			if (res == null || res.getSingleResult() == null)
				res = constructDAO.findByField("modInternalId", dto.getConstructIdentifier());
			if (res == null || res.getSingleResult() == null) {
				assocResponse.addErrorMessage("construct_identifier", ValidationConstants.INVALID_MESSAGE);
			} else {
				construct = res.getSingleResult();
				if (beDataProvider != null && !construct.getDataProvider().getSourceOrganization().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
					assocResponse.addErrorMessage("construct_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load");
				}
			}
		} else {
			assocResponse.addErrorMessage("construct_identifier", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		ConstructGenomicEntityAssociation association = null;
		if (construct != null && StringUtils.isNotBlank(dto.getGenomicEntityRelationName()) && StringUtils.isNotBlank(dto.getGenomicEntityCurie())) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("subject.id", construct.getId());
			params.put("relation.name", dto.getGenomicEntityRelationName());
			params.put("object.curie", dto.getGenomicEntityCurie());
			
			SearchResponse<ConstructGenomicEntityAssociation> searchResponse = constructGenomicEntityAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			}
		}
		if (association == null)
			association = new ConstructGenomicEntityAssociation();
		
		association.setSubject(construct);
		
		ObjectResponse<ConstructGenomicEntityAssociation> eviResponse = validateEvidenceAssociationDTO(association, dto);
		assocResponse.addErrorMessages(eviResponse.getErrorMessages());
		association = eviResponse.getEntity();
		
		if (StringUtils.isBlank(dto.getGenomicEntityCurie())) {
			assocResponse.addErrorMessage("genomic_entity_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			GenomicEntity genomicEntity = genomicEntityDAO.find(dto.getGenomicEntityCurie());
			if (genomicEntity == null)
				assocResponse.addErrorMessage("genomic_entity_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGenomicEntityCurie() + ")");
			association.setObject(genomicEntity);
		}
		
		if (StringUtils.isNotEmpty(dto.getGenomicEntityRelationName())) {
			VocabularyTerm relation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.CONSTRUCT_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET, dto.getGenomicEntityRelationName()).getEntity();
			if (relation == null)
				assocResponse.addErrorMessage("genomic_entity_relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGenomicEntityRelationName() + ")");
			association.setRelation(relation);
		} else {
			assocResponse.addErrorMessage("genomic_entity_relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (CollectionUtils.isNotEmpty(association.getRelatedNotes())) {
			association.getRelatedNotes().forEach(note -> {
				constructGenomicEntityAssociationDAO.deleteAttachedNote(note.getId());
			});
		}
		if (CollectionUtils.isNotEmpty(dto.getNoteDtos())) {
			List<Note> notes = new ArrayList<>();
			Set<String> noteIdentities = new HashSet<>();
			for (NoteDTO noteDTO : dto.getNoteDtos()) {
				ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(noteDTO, VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
				if (noteResponse.hasErrors()) {
					assocResponse.addErrorMessage("note_dtos", noteResponse.errorMessagesString());
					break;
				}
				String noteIdentity = NoteIdentityHelper.noteDtoIdentity(noteDTO);
				if (!noteIdentities.contains(noteIdentity)) {
					noteIdentities.add(noteIdentity);
					notes.add(noteDAO.persist(noteResponse.getEntity()));
				}
			}
			association.setRelatedNotes(notes);
		} else {
			association.setRelatedNotes(null);
		}
		
		if (assocResponse.hasErrors()) {
			log.info(assocResponse.errorMessagesString());
			throw new ObjectValidationException(dto, assocResponse.errorMessagesString());
		}
		association = constructGenomicEntityAssociationDAO.persist(association);
		
		return association;
	}
}
