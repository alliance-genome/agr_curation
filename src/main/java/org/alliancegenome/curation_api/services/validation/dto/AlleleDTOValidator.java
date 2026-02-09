package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleDatabaseStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.AlleleFunctionalImpactSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.AlleleInheritanceModeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.AlleleMutationTypeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.AlleleNomenclatureEventSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.SlotAnnotationIdentityHelper;
import org.alliancegenome.curation_api.services.validation.dto.base.GenomicEntityDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AlleleDatabaseStatusSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AlleleFullNameSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AlleleFunctionalImpactSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AlleleInheritanceModeSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AlleleMutationTypeSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AlleleNomenclatureEventSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AlleleSecondaryIdSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AlleleSymbolSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AlleleSynonymSlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleDTOValidator extends GenomicEntityDTOValidator<Allele, AlleleDTO> {

	@Inject
	AlleleDAO alleleDAO;
	@Inject
	AlleleMutationTypeSlotAnnotationDTOValidator alleleMutationTypeDtoValidator;
	@Inject
	AlleleInheritanceModeSlotAnnotationDTOValidator alleleInheritanceModeDtoValidator;
	@Inject
	AlleleGermlineTransmissionStatusSlotAnnotationDTOValidator alleleGermlineTransmissionStatusDtoValidator;
	@Inject
	AlleleNomenclatureEventSlotAnnotationDTOValidator alleleNomenclatureEventDtoValidator;
	@Inject
	AlleleSymbolSlotAnnotationDTOValidator alleleSymbolDtoValidator;
	@Inject
	AlleleFullNameSlotAnnotationDTOValidator alleleFullNameDtoValidator;
	@Inject
	AlleleSynonymSlotAnnotationDTOValidator alleleSynonymDtoValidator;
	@Inject
	AlleleSecondaryIdSlotAnnotationDTOValidator alleleSecondaryIdDtoValidator;
	@Inject
	AlleleDatabaseStatusSlotAnnotationDTOValidator alleleDatabaseStatusDtoValidator;
	@Inject
	SlotAnnotationIdentityHelper identityHelper;
	@Inject
	AlleleFunctionalImpactSlotAnnotationDTOValidator alleleFunctionalImpactDtoValidator;
	@Inject
	NoteDTOValidator noteDtoValidator;

	@Transactional
	public ObjectResponse<Allele> validateAlleleDTO(AlleleDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<>();

		Allele allele = findDatabaseObject(alleleDAO, "primaryExternalId", "primary_external_id", dto.getPrimaryExternalId());
		if (allele == null) {
			allele = new Allele();
		}

		allele = validateGenomicEntityDTO(allele, dto, dataProvider, VocabularyConstants.ALLELE_NOTE_TYPES_VOCABULARY_TERM_SET);

		VocabularyTerm inCollection = validateTermInVocabulary("in_collection_name", dto.getInCollectionName(), VocabularyConstants.ALLELE_COLLECTION_VOCABULARY);
		allele.setInCollection(inCollection);

		allele.setIsExtinct(dto.getIsExtinct());

		List<Reference> refs = validateReferences("reference_curies", dto.getReferenceCuries(), false);
		allele.setReferences(refs);

		List<AlleleMutationTypeSlotAnnotation> mutationTypes = validateAlleleMutationTypes(allele, dto);
		if (allele.getAlleleMutationTypes() != null) {
			allele.getAlleleMutationTypes().clear();
		}
		if (mutationTypes != null) {
			if (allele.getAlleleMutationTypes() == null) {
				allele.setAlleleMutationTypes(new ArrayList<>());
			}
			allele.getAlleleMutationTypes().addAll(mutationTypes);
		}

		List<AlleleInheritanceModeSlotAnnotation> inheritanceModes = validateAlleleInheritanceModes(allele, dto);
		if (allele.getAlleleInheritanceModes() != null) {
			allele.getAlleleInheritanceModes().clear();
		}
		if (inheritanceModes != null) {
			if (allele.getAlleleInheritanceModes() == null) {
				allele.setAlleleInheritanceModes(new ArrayList<>());
			}
			allele.getAlleleInheritanceModes().addAll(inheritanceModes);
		}

		AlleleGermlineTransmissionStatusSlotAnnotation germlineTransmissionStatus = validateAlleleGermlineTransmissionStatus(allele, dto);
		allele.setAlleleGermlineTransmissionStatus(germlineTransmissionStatus);

		AlleleDatabaseStatusSlotAnnotation databaseStatus = validateAlleleDatabaseStatus(allele, dto);
		allele.setAlleleDatabaseStatus(databaseStatus);

		List<AlleleNomenclatureEventSlotAnnotation> nomenclatureEvents = validateAlleleNomenclatureEvents(allele, dto);
		if (allele.getAlleleNomenclatureEvents() != null) {
			allele.getAlleleNomenclatureEvents().clear();
		}
		if (nomenclatureEvents != null) {
			if (allele.getAlleleNomenclatureEvents() == null) {
				allele.setAlleleNomenclatureEvents(new ArrayList<>());
			}
			allele.getAlleleNomenclatureEvents().addAll(nomenclatureEvents);
		}

		AlleleSymbolSlotAnnotation symbol = validateAlleleSymbol(allele, dto);
		allele.setAlleleSymbol(symbol);

		AlleleFullNameSlotAnnotation fullName = validateAlleleFullName(allele, dto);
		allele.setAlleleFullName(fullName);

		List<AlleleSynonymSlotAnnotation> synonyms = validateAlleleSynonyms(allele, dto);
		if (allele.getAlleleSynonyms() != null) {
			allele.getAlleleSynonyms().clear();
		}
		if (synonyms != null) {
			if (allele.getAlleleSynonyms() == null) {
				allele.setAlleleSynonyms(new ArrayList<>());
			}
			allele.getAlleleSynonyms().addAll(synonyms);
		}

		List<AlleleSecondaryIdSlotAnnotation> secondaryIds = validateAlleleSecondaryIds(allele, dto);
		if (allele.getAlleleSecondaryIds() != null) {
			allele.getAlleleSecondaryIds().clear();
		}
		if (secondaryIds != null) {
			if (allele.getAlleleSecondaryIds() == null) {
				allele.setAlleleSecondaryIds(new ArrayList<>());
			}
			allele.getAlleleSecondaryIds().addAll(secondaryIds);
		}

		List<AlleleFunctionalImpactSlotAnnotation> functionalImpacts = validateAlleleFunctionalImpacts(allele, dto);
		if (allele.getAlleleFunctionalImpacts() != null) {
			allele.getAlleleFunctionalImpacts().clear();
		}
		if (functionalImpacts != null) {
			if (allele.getAlleleFunctionalImpacts() == null) {
				allele.setAlleleFunctionalImpacts(new ArrayList<>());
			}
			allele.getAlleleFunctionalImpacts().addAll(functionalImpacts);
		}

		response.convertWarningMessagesToMap();
		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}
		
		try {
			response.setEntity(alleleDAO.persist(allele));
			return response;
		} catch (Exception e) {
			response.addErrorMessages("", null, e.getMessage());
			throw new ObjectValidationException(dto, e.getMessage());
		}

	}

	private List<AlleleMutationTypeSlotAnnotation> validateAlleleMutationTypes(Allele allele, AlleleDTO dto) {
		String field = "allele_mutation_type_dtos";

		Map<String, AlleleMutationTypeSlotAnnotation> existingMutationTypes = new HashMap<>();
		if (CollectionUtils.isNotEmpty(allele.getAlleleMutationTypes())) {
			for (AlleleMutationTypeSlotAnnotation existingMutationType : allele.getAlleleMutationTypes()) {
				existingMutationTypes.put(SlotAnnotationIdentityHelper.alleleMutationTypeIdentity(existingMutationType), existingMutationType);
			}
		}

		List<AlleleMutationTypeSlotAnnotation> validatedMutationTypes = new ArrayList<AlleleMutationTypeSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getAlleleMutationTypeDtos())) {
			for (int ix = 0; ix < dto.getAlleleMutationTypeDtos().size(); ix++) {
				AlleleMutationTypeSlotAnnotationDTO mtDto = dto.getAlleleMutationTypeDtos().get(ix);
				AlleleMutationTypeSlotAnnotation mt = existingMutationTypes.remove(identityHelper.alleleMutationTypeDtoIdentity(mtDto));
				ObjectResponse<AlleleMutationTypeSlotAnnotation> mtResponse = alleleMutationTypeDtoValidator.validateAlleleMutationTypeSlotAnnotationDTO(mt, mtDto);
				if (mtResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, mtResponse.getErrorMessages());
				} else {
					mt = mtResponse.getEntity();
					mt.setSingleAllele(allele);
					validatedMutationTypes.add(mt);
				}
			}
		}

		if (!allValid) {
			response.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedMutationTypes)) {
			return null;
		}

		return validatedMutationTypes;
	}

	private List<AlleleInheritanceModeSlotAnnotation> validateAlleleInheritanceModes(Allele allele, AlleleDTO dto) {
		String field = "allele_inheritance_mode_dtos";

		Map<String, AlleleInheritanceModeSlotAnnotation> existingInheritanceModes = new HashMap<>();
		if (CollectionUtils.isNotEmpty(allele.getAlleleInheritanceModes())) {
			for (AlleleInheritanceModeSlotAnnotation existingInheritanceMode : allele.getAlleleInheritanceModes()) {
				existingInheritanceModes.put(SlotAnnotationIdentityHelper.alleleInheritanceModeIdentity(existingInheritanceMode), existingInheritanceMode);
			}
		}

		List<AlleleInheritanceModeSlotAnnotation> validatedInheritanceModes = new ArrayList<AlleleInheritanceModeSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getAlleleInheritanceModeDtos())) {
			for (int ix = 0; ix < dto.getAlleleInheritanceModeDtos().size(); ix++) {
				AlleleInheritanceModeSlotAnnotationDTO imDto = dto.getAlleleInheritanceModeDtos().get(ix);
				AlleleInheritanceModeSlotAnnotation im = existingInheritanceModes.remove(identityHelper.alleleInheritanceModeDtoIdentity(imDto));
				ObjectResponse<AlleleInheritanceModeSlotAnnotation> imResponse = alleleInheritanceModeDtoValidator.validateAlleleInheritanceModeSlotAnnotationDTO(im, imDto);
				if (imResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, imResponse.getErrorMessages());
				} else {
					im = imResponse.getEntity();
					im.setSingleAllele(allele);
					validatedInheritanceModes.add(im);
				}
			}
		}

		if (!allValid) {
			response.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedInheritanceModes)) {
			return null;
		}

		return validatedInheritanceModes;
	}

	private AlleleGermlineTransmissionStatusSlotAnnotation validateAlleleGermlineTransmissionStatus(Allele allele, AlleleDTO dto) {
		if (dto.getAlleleGermlineTransmissionStatusDto() == null) {
			return null;
		}

		String field = "allele_germline_transmission_status_dto";

		ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> agtsResponse = alleleGermlineTransmissionStatusDtoValidator.validateAlleleGermlineTransmissionStatusSlotAnnotationDTO(allele.getAlleleGermlineTransmissionStatus(), dto.getAlleleGermlineTransmissionStatusDto());
		if (agtsResponse.hasErrors()) {
			response.addErrorMessage(field, agtsResponse.errorMessagesString());
			response.addErrorMessages(field, agtsResponse.getErrorMessages());
			return null;
		}

		AlleleGermlineTransmissionStatusSlotAnnotation agts = agtsResponse.getEntity();
		agts.setSingleAllele(allele);

		return agts;
	}

	private AlleleDatabaseStatusSlotAnnotation validateAlleleDatabaseStatus(Allele allele, AlleleDTO dto) {
		if (dto.getAlleleDatabaseStatusDto() == null) {
			return null;
		}

		String field = "allele_database_status_dto";

		ObjectResponse<AlleleDatabaseStatusSlotAnnotation> adsResponse = alleleDatabaseStatusDtoValidator.validateAlleleDatabaseStatusSlotAnnotationDTO(allele.getAlleleDatabaseStatus(), dto.getAlleleDatabaseStatusDto());
		if (adsResponse.hasErrors()) {
			response.addErrorMessage(field, adsResponse.errorMessagesString());
			response.addErrorMessages(field, adsResponse.getErrorMessages());
			return null;
		}

		AlleleDatabaseStatusSlotAnnotation ads = adsResponse.getEntity();
		ads.setSingleAllele(allele);

		return ads;
	}

	private List<AlleleNomenclatureEventSlotAnnotation> validateAlleleNomenclatureEvents(Allele allele, AlleleDTO dto) {
		String field = "allele_nomenclature_event_dtos";

		Map<String, AlleleNomenclatureEventSlotAnnotation> existingNomenclatureEvents = new HashMap<>();
		if (CollectionUtils.isNotEmpty(allele.getAlleleNomenclatureEvents())) {
			for (AlleleNomenclatureEventSlotAnnotation existingNomenclatureEvent : allele.getAlleleNomenclatureEvents()) {
				existingNomenclatureEvents.put(SlotAnnotationIdentityHelper.alleleNomenclatureEventIdentity(existingNomenclatureEvent), existingNomenclatureEvent);
			}
		}

		List<AlleleNomenclatureEventSlotAnnotation> validatedNomenclatureEvents = new ArrayList<AlleleNomenclatureEventSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getAlleleNomenclatureEventDtos())) {
			for (int ix = 0; ix < dto.getAlleleNomenclatureEventDtos().size(); ix++) {
				AlleleNomenclatureEventSlotAnnotationDTO neDto = dto.getAlleleNomenclatureEventDtos().get(ix);
				AlleleNomenclatureEventSlotAnnotation ne = existingNomenclatureEvents.remove(identityHelper.alleleNomenclatureEventDtoIdentity(neDto));
				ObjectResponse<AlleleNomenclatureEventSlotAnnotation> neResponse = alleleNomenclatureEventDtoValidator.validateAlleleNomenclatureEventSlotAnnotationDTO(ne, neDto);
				if (neResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, neResponse.getErrorMessages());
				} else {
					ne = neResponse.getEntity();
					ne.setSingleAllele(allele);
					validatedNomenclatureEvents.add(ne);
				}
			}
		}

		if (!allValid) {
			response.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedNomenclatureEvents)) {
			return null;
		}

		return validatedNomenclatureEvents;
	}

	private AlleleSymbolSlotAnnotation validateAlleleSymbol(Allele allele, AlleleDTO dto) {
		String field = "allele_symbol_dto";

		if (dto.getAlleleSymbolDto() == null) {
			response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<AlleleSymbolSlotAnnotation> symbolResponse = alleleSymbolDtoValidator.validateAlleleSymbolSlotAnnotationDTO(allele.getAlleleSymbol(), dto.getAlleleSymbolDto());
		if (symbolResponse.hasErrors()) {
			response.addErrorMessage(field, symbolResponse.errorMessagesString());
			response.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		AlleleSymbolSlotAnnotation symbol = symbolResponse.getEntity();
		symbol.setSingleAllele(allele);

		return symbol;
	}

	private AlleleFullNameSlotAnnotation validateAlleleFullName(Allele allele, AlleleDTO dto) {
		if (dto.getAlleleFullNameDto() == null) {
			return null;
		}

		String field = "allele_full_name_dto";

		ObjectResponse<AlleleFullNameSlotAnnotation> nameResponse = alleleFullNameDtoValidator.validateAlleleFullNameSlotAnnotationDTO(allele.getAlleleFullName(), dto.getAlleleFullNameDto());
		if (nameResponse.hasErrors()) {
			response.addErrorMessage(field, nameResponse.errorMessagesString());
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		AlleleFullNameSlotAnnotation fullName = nameResponse.getEntity();
		fullName.setSingleAllele(allele);

		return fullName;
	}

	private List<AlleleSynonymSlotAnnotation> validateAlleleSynonyms(Allele allele, AlleleDTO dto) {
		String field = "allele_synonym_dtos";

		Map<String, AlleleSynonymSlotAnnotation> existingSynonyms = new HashMap<>();
		if (CollectionUtils.isNotEmpty(allele.getAlleleSynonyms())) {
			for (AlleleSynonymSlotAnnotation existingSynonym : allele.getAlleleSynonyms()) {
				existingSynonyms.put(SlotAnnotationIdentityHelper.nameSlotAnnotationIdentity(existingSynonym), existingSynonym);
			}
		}

		List<AlleleSynonymSlotAnnotation> validatedSynonyms = new ArrayList<AlleleSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getAlleleSynonymDtos())) {
			for (int ix = 0; ix < dto.getAlleleSynonymDtos().size(); ix++) {
				NameSlotAnnotationDTO synDto = dto.getAlleleSynonymDtos().get(ix);
				AlleleSynonymSlotAnnotation syn = existingSynonyms.remove(identityHelper.nameSlotAnnotationDtoIdentity(synDto));
				ObjectResponse<AlleleSynonymSlotAnnotation> synResponse = alleleSynonymDtoValidator.validateAlleleSynonymSlotAnnotationDTO(syn, synDto);
				if (synResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
				} else {
					syn = synResponse.getEntity();
					syn.setSingleAllele(allele);
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

	private List<AlleleSecondaryIdSlotAnnotation> validateAlleleSecondaryIds(Allele allele, AlleleDTO dto) {
		String field = "allele_secondary_id_dtos";

		Map<String, AlleleSecondaryIdSlotAnnotation> existingSecondaryIds = new HashMap<>();
		if (CollectionUtils.isNotEmpty(allele.getAlleleSecondaryIds())) {
			for (AlleleSecondaryIdSlotAnnotation existingSecondaryId : allele.getAlleleSecondaryIds()) {
				existingSecondaryIds.put(SlotAnnotationIdentityHelper.secondaryIdIdentity(existingSecondaryId), existingSecondaryId);
			}
		}

		List<AlleleSecondaryIdSlotAnnotation> validatedSecondaryIds = new ArrayList<AlleleSecondaryIdSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getAlleleSecondaryIdDtos())) {
			for (int ix = 0; ix < dto.getAlleleSecondaryIdDtos().size(); ix++) {
				SecondaryIdSlotAnnotationDTO sidDto = dto.getAlleleSecondaryIdDtos().get(ix);
				AlleleSecondaryIdSlotAnnotation sid = existingSecondaryIds.remove(identityHelper.secondaryIdDtoIdentity(sidDto));
				ObjectResponse<AlleleSecondaryIdSlotAnnotation> sidResponse = alleleSecondaryIdDtoValidator.validateAlleleSecondaryIdSlotAnnotationDTO(sid, sidDto);
				if (sidResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, sidResponse.getErrorMessages());
				} else {
					sid = sidResponse.getEntity();
					sid.setSingleAllele(allele);
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

	private List<AlleleFunctionalImpactSlotAnnotation> validateAlleleFunctionalImpacts(Allele allele, AlleleDTO dto) {
		String field = "allele_functional_impact_dtos";

		Map<String, AlleleFunctionalImpactSlotAnnotation> existingFunctionalImpacts = new HashMap<>();
		if (CollectionUtils.isNotEmpty(allele.getAlleleFunctionalImpacts())) {
			for (AlleleFunctionalImpactSlotAnnotation existingFunctionalImpact : allele.getAlleleFunctionalImpacts()) {
				existingFunctionalImpacts.put(SlotAnnotationIdentityHelper.alleleFunctionalImpactIdentity(existingFunctionalImpact), existingFunctionalImpact);
			}
		}

		List<AlleleFunctionalImpactSlotAnnotation> validatedFunctionalImpacts = new ArrayList<AlleleFunctionalImpactSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getAlleleFunctionalImpactDtos())) {
			for (int ix = 0; ix < dto.getAlleleFunctionalImpactDtos().size(); ix++) {
				AlleleFunctionalImpactSlotAnnotationDTO fiDto = dto.getAlleleFunctionalImpactDtos().get(ix);
				AlleleFunctionalImpactSlotAnnotation fi = existingFunctionalImpacts.remove(identityHelper.alleleFunctionalImpactDtoIdentity(fiDto));
				ObjectResponse<AlleleFunctionalImpactSlotAnnotation> fiResponse = alleleFunctionalImpactDtoValidator.validateAlleleFunctionalImpactSlotAnnotationDTO(fi, fiDto);
				if (fiResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, fiResponse.getErrorMessages());
				} else {
					fi = fiResponse.getEntity();
					fi.setSingleAllele(allele);
					validatedFunctionalImpacts.add(fi);
				}
			}
		}

		if (!allValid) {
			response.convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedFunctionalImpacts)) {
			return null;
		}

		return validatedFunctionalImpacts;
	}
}
