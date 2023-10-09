package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AnnotationDAO;
import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.AnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.ontology.DoTermService;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AnnotationDTOValidator extends BaseDTOValidator {

	@Inject
	ReferenceService referenceService;
	@Inject
	NoteDAO noteDAO;
	@Inject
	AnnotationDAO annotationDAO;
	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	ConditionRelationDTOValidator conditionRelationDtoValidator;
	@Inject
	NoteDTOValidator noteDtoValidator;
	@Inject
	DataProviderDTOValidator dataProviderDtoValidator;
	@Inject
	DataProviderDAO dataProviderDAO;

	public <E extends Annotation, D extends AnnotationDTO> ObjectResponse<E> validateAnnotationDTO(E annotation, D dto, String noteTypeSet) {
		ObjectResponse<E> annotResponse = validateAuditedObjectDTO(annotation, dto);
		annotation = annotResponse.getEntity();

		if (StringUtils.isNotBlank(dto.getModEntityId())) {
			annotation.setModEntityId(dto.getModEntityId());
		} else {
			annotation.setModEntityId(null);
		}
		
		if (StringUtils.isNotBlank(dto.getModInternalId())) {
			annotation.setModInternalId(dto.getModInternalId());
		} else {
			annotation.setModInternalId(null);
		}
		
		if (dto.getDataProviderDto() == null) {
			annotResponse.addErrorMessage("data_provider_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<DataProvider> dpResponse = dataProviderDtoValidator.validateDataProviderDTO(dto.getDataProviderDto(), annotation.getDataProvider());
			if (dpResponse.hasErrors()) {
				annotResponse.addErrorMessage("data_provider_dto", dpResponse.errorMessagesString());
			} else {
				annotation.setDataProvider(dataProviderDAO.persist(dpResponse.getEntity()));
			}
		}

		if (CollectionUtils.isNotEmpty(annotation.getRelatedNotes())) {
			annotation.getRelatedNotes().forEach(note -> {
				annotationDAO.deleteAttachedNote(note.getId());
			});
		}
		if (CollectionUtils.isNotEmpty(dto.getNoteDtos())) {
			List<Note> notes = new ArrayList<>();
			Set<String> noteIdentities = new HashSet<>();
			for (NoteDTO noteDTO : dto.getNoteDtos()) {
				ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(noteDTO, noteTypeSet);
				if (noteResponse.hasErrors()) {
					annotResponse.addErrorMessage("note_dtos", noteResponse.errorMessagesString());
					break;
				}
				if (CollectionUtils.isNotEmpty(noteDTO.getEvidenceCuries())) {
					for (String noteRef : noteDTO.getEvidenceCuries()) {
						if (!noteRef.equals(dto.getReferenceCurie())) {
							annotResponse.addErrorMessage("relatedNotes - evidence_curies", ValidationConstants.INVALID_MESSAGE + " (" + noteRef + ")");
						}
					}
				}
				String noteIdentity = NoteIdentityHelper.noteDtoIdentity(noteDTO);
				if (!noteIdentities.contains(noteIdentity)) {
					noteIdentities.add(noteIdentity);
					notes.add(noteDAO.persist(noteResponse.getEntity()));
				}
			}
			annotation.setRelatedNotes(notes);
		} else {
			annotation.setRelatedNotes(null);
		}

		if (CollectionUtils.isNotEmpty(dto.getConditionRelationDtos())) {
			List<ConditionRelation> relations = new ArrayList<>();
			for (ConditionRelationDTO conditionRelationDTO : dto.getConditionRelationDtos()) {
				if (StringUtils.isNotBlank(conditionRelationDTO.getHandle())) {
					if (!conditionRelationDTO.getReferenceCurie().equals(dto.getReferenceCurie())) {
						annotResponse.addErrorMessage("condition_relation_dtos - reference_curie", ValidationConstants.INVALID_MESSAGE + " (" + conditionRelationDTO.getReferenceCurie() + ")");
					}
				}
				ObjectResponse<ConditionRelation> crResponse = conditionRelationDtoValidator.validateConditionRelationDTO(conditionRelationDTO);
				if (crResponse.hasErrors()) {
					annotResponse.addErrorMessage("condition_relation_dtos", crResponse.errorMessagesString());
				} else {
					relations.add(conditionRelationDAO.persist(crResponse.getEntity()));
				}
			}
			annotation.setConditionRelations(relations);
		} else {
			annotation.setConditionRelations(null);
		}

		annotResponse.setEntity(annotation);
		
		return annotResponse;
	}

	public <E extends Annotation, D extends AnnotationDTO> ObjectResponse<E> validateReference(E annotation, D dto) {
		ObjectResponse<E> aResponse = new ObjectResponse<E>();

		Reference reference = null;
		if (StringUtils.isBlank(dto.getReferenceCurie())) {
			aResponse.addErrorMessage("reference_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			reference = referenceService.retrieveFromDbOrLiteratureService(dto.getReferenceCurie());
			if (reference == null)
				aResponse.addErrorMessage("reference_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getReferenceCurie() + ")");
		}
		annotation.setSingleReference(reference);

		aResponse.setEntity(annotation);
		return aResponse;
	}
}
