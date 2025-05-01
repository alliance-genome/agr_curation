package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.SlotAnnotationIdentityHelper;
import org.alliancegenome.curation_api.services.validation.dto.base.GenomicEntityDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AgmFullNameSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AgmSecondaryIdSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AgmSynonymSlotAnnotationDTOValidator;
import org.apache.commons.collections4.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AffectedGenomicModelDTOValidator extends GenomicEntityDTOValidator<AffectedGenomicModel, AffectedGenomicModelDTO> {

	@Inject AffectedGenomicModelDAO affectedGenomicModelDAO;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject SynonymDAO synonymDAO;
	@Inject SlotAnnotationIdentityHelper identityHelper;
	@Inject AgmFullNameSlotAnnotationDTOValidator agmFullNameDtoValidator;
	@Inject AgmSynonymSlotAnnotationDTOValidator agmSynonymDtoValidator;
	@Inject AgmSecondaryIdSlotAnnotationDTOValidator agmSecondaryIdDtoValidator;

	@Transactional
	public AffectedGenomicModel validateAffectedGenomicModelDTO(AffectedGenomicModelDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<AffectedGenomicModel>();
		
		AffectedGenomicModel agm = findDatabaseObject(affectedGenomicModelDAO, "primaryExternalId", "primary_external_id", dto.getPrimaryExternalId());
		if (agm == null) {
			agm = new AffectedGenomicModel();
		}
		
		agm = validateGenomicEntityDTO(agm, dto, dataProvider);

		AgmFullNameSlotAnnotation fullName = validateAgmFullName(agm, dto);
		agm.setAgmFullName(fullName);
		
		List<AgmSynonymSlotAnnotation> synonyms = validateAgmSynonyms(agm, dto);
		if (agm.getAgmSynonyms() != null) {
			agm.getAgmSynonyms().clear();
		}
		if (synonyms != null) {
			if (agm.getAgmSynonyms() == null) {
				agm.setAgmSynonyms(new ArrayList<>());
			}
			agm.getAgmSynonyms().addAll(synonyms);
		}

		List<AgmSecondaryIdSlotAnnotation> secondaryIds = validateAgmSecondaryIds(agm, dto);
		if (agm.getAgmSecondaryIds() != null) {
			agm.getAgmSecondaryIds().clear();
		}
		if (secondaryIds != null) {
			if (agm.getAgmSecondaryIds() == null) {
				agm.setAgmSecondaryIds(new ArrayList<>());
			}
			agm.getAgmSecondaryIds().addAll(secondaryIds);
		}

		VocabularyTerm subtype = validateRequiredTermInVocabulary("subtype_name", dto.getSubtypeName(), VocabularyConstants.AGM_SUBTYPE_VOCABULARY);
		agm.setSubtype(subtype);

		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		return affectedGenomicModelDAO.persist(agm);
	}

	private AgmFullNameSlotAnnotation validateAgmFullName(AffectedGenomicModel agm, AffectedGenomicModelDTO dto) {
		String field = "agm_full_name_dto";
		if (dto.getAgmFullNameDto() == null) {
			return null;
		}

		ObjectResponse<AgmFullNameSlotAnnotation> nameResponse = agmFullNameDtoValidator.validateAgmFullNameSlotAnnotationDTO(agm.getAgmFullName(), dto.getAgmFullNameDto());
		if (nameResponse.hasErrors()) {
			response.addErrorMessage(field, nameResponse.errorMessagesString());
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		AgmFullNameSlotAnnotation fullName = nameResponse.getEntity();
		fullName.setSingleAgm(agm);

		return fullName;
	}

	private List<AgmSynonymSlotAnnotation> validateAgmSynonyms(AffectedGenomicModel agm, AffectedGenomicModelDTO dto) {
		String field = "agm_synonym_dtos";

		Map<String, AgmSynonymSlotAnnotation> existingSynonyms = new HashMap<>();
		if (CollectionUtils.isNotEmpty(agm.getAgmSynonyms())) {
			for (AgmSynonymSlotAnnotation existingSynonym : agm.getAgmSynonyms()) {
				existingSynonyms.put(SlotAnnotationIdentityHelper.nameSlotAnnotationIdentity(existingSynonym), existingSynonym);
			}
		}

		List<AgmSynonymSlotAnnotation> validatedSynonyms = new ArrayList<AgmSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getAgmSynonymDtos())) {
			for (int ix = 0; ix < dto.getAgmSynonymDtos().size(); ix++) {
				NameSlotAnnotationDTO synDto = dto.getAgmSynonymDtos().get(ix);
				AgmSynonymSlotAnnotation syn = existingSynonyms.remove(identityHelper.nameSlotAnnotationDtoIdentity(synDto));
				ObjectResponse<AgmSynonymSlotAnnotation> synResponse = agmSynonymDtoValidator.validateAgmSynonymSlotAnnotationDTO(syn, synDto);
				if (synResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
				} else {
					syn = synResponse.getEntity();
					syn.setSingleAgm(agm);
					validatedSynonyms.add(syn);
				}
			}
		}

		if (!allValid) {
			response.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSynonyms)) {
			return null;
		}

		return validatedSynonyms;
	}

	private List<AgmSecondaryIdSlotAnnotation> validateAgmSecondaryIds(AffectedGenomicModel model, AffectedGenomicModelDTO dto) {
		String field = "agm_secondary_id_dtos";

		Map<String, AgmSecondaryIdSlotAnnotation> existingSecondaryIds = new HashMap<>();
		if (CollectionUtils.isNotEmpty(model.getAgmSecondaryIds())) {
			for (AgmSecondaryIdSlotAnnotation existingSecondaryId : model.getAgmSecondaryIds()) {
				existingSecondaryIds.put(SlotAnnotationIdentityHelper.secondaryIdIdentity(existingSecondaryId), existingSecondaryId);
			}
		}

		List<AgmSecondaryIdSlotAnnotation> validatedSecondaryIds = new ArrayList<>();
		boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getAgmSecondaryIdDtos())) {
			for (int ix = 0; ix < dto.getAgmSecondaryIdDtos().size(); ix++) {
				SecondaryIdSlotAnnotationDTO sidDto = dto.getAgmSecondaryIdDtos().get(ix);
				AgmSecondaryIdSlotAnnotation sid = existingSecondaryIds.remove(identityHelper.secondaryIdDtoIdentity(sidDto));
				ObjectResponse<AgmSecondaryIdSlotAnnotation> sidResponse = agmSecondaryIdDtoValidator.validateAgmSecondaryIdSlotAnnotationDTO(sid, sidDto);
				if (sidResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, sidResponse.getErrorMessages());
				} else {
					sid = sidResponse.getEntity();
					sid.setSingleAgm(model);
					validatedSecondaryIds.add(sid);
				}
			}
		}

		if (!allValid) {
			response.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSecondaryIds)) {
			return null;
		}

		return validatedSecondaryIds;
	}

}
