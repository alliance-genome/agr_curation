package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.helpers.slotAnnotations.SlotAnnotationIdentityHelper;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleDatabaseStatusSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleNomenclatureEventSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleDTOValidator extends BaseDTOValidator {

	@Inject
	AlleleDAO alleleDAO;
	@Inject
	AlleleMutationTypeSlotAnnotationDAO alleleMutationTypeDAO;
	@Inject
	AlleleInheritanceModeSlotAnnotationDAO alleleInheritanceModeDAO;
	@Inject
	AlleleGermlineTransmissionStatusSlotAnnotationDAO alleleGermlineTransmissionStatusDAO;
	@Inject
	AlleleNomenclatureEventSlotAnnotationDAO alleleNomenclatureEventDAO;
	@Inject
	AlleleSymbolSlotAnnotationDAO alleleSymbolDAO;
	@Inject
	AlleleFullNameSlotAnnotationDAO alleleFullNameDAO;
	@Inject
	AlleleSynonymSlotAnnotationDAO alleleSynonymDAO;
	@Inject
	AlleleSecondaryIdSlotAnnotationDAO alleleSecondaryIdDAO;
	@Inject
	AlleleFunctionalImpactSlotAnnotationDAO alleleFunctionalImpactDAO;
	@Inject
	AlleleDatabaseStatusSlotAnnotationDAO alleleDatabaseStatusDAO;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	ReferenceDAO referenceDAO;
	@Inject
	NoteDAO noteDAO;
	@Inject
	ReferenceService referenceService;
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

	private ObjectResponse<Allele> alleleResponse = new ObjectResponse<>();
	
	@Transactional
	public Allele validateAlleleDTO(AlleleDTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {
		
		Allele allele = null;
		if (StringUtils.isBlank(dto.getCurie())) {
			alleleResponse.addErrorMessage("curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			allele = alleleDAO.find(dto.getCurie());
		}

		if (allele == null)
			allele = new Allele();

		allele.setCurie(dto.getCurie());

		ObjectResponse<Allele> geResponse = validateGenomicEntityDTO(allele, dto, dataProvider);
		alleleResponse.addErrorMessages(geResponse.getErrorMessages());

		allele = geResponse.getEntity();

		VocabularyTerm inCollection = null;
		if (StringUtils.isNotBlank(dto.getInCollectionName())) {
			inCollection = vocabularyTermService.getTermInVocabulary(VocabularyConstants.ALLELE_COLLECTION_VOCABULARY, dto.getInCollectionName()).getEntity();
			if (inCollection == null) {
				alleleResponse.addErrorMessage("in_collection_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getInCollectionName() + ")");
			}
		}
		allele.setInCollection(inCollection);

		allele.setIsExtinct(dto.getIsExtinct());

		if (CollectionUtils.isNotEmpty(dto.getReferenceCuries())) {
			List<Reference> references = new ArrayList<>();
			for (String publicationId : dto.getReferenceCuries()) {
				Reference reference = referenceService.retrieveFromDbOrLiteratureService(publicationId);
				if (reference == null) {
					alleleResponse.addErrorMessage("reference_curies", ValidationConstants.INVALID_MESSAGE + " (" + publicationId + ")");
				} else {
					references.add(reference);
				}
			}
			allele.setReferences(references);
		} else {
			allele.setReferences(null);
		}
		
		List<Note> relatedNotes = validateRelatedNotes(allele, dto);
		if (relatedNotes != null) {
			if (allele.getRelatedNotes() == null)
				allele.setRelatedNotes(new ArrayList<>());
			allele.getRelatedNotes().addAll(relatedNotes);
		}

		List<AlleleMutationTypeSlotAnnotation> mutationTypes = validateAlleleMutationTypes(allele, dto);
		List<AlleleInheritanceModeSlotAnnotation> inheritanceModes = validateAlleleInheritanceModes(allele, dto);
		AlleleGermlineTransmissionStatusSlotAnnotation germlineTransmissionStatus = validateAlleleGermlineTransmissionStatus(allele, dto);
		AlleleDatabaseStatusSlotAnnotation databaseStatus = validateAlleleDatabaseStatus(allele, dto);
		List<AlleleNomenclatureEventSlotAnnotation> nomenclatureEvents = validateAlleleNomenclatureEvents(allele, dto);
		AlleleSymbolSlotAnnotation symbol = validateAlleleSymbol(allele, dto);
		AlleleFullNameSlotAnnotation fullName = validateAlleleFullName(allele, dto);
		List<AlleleSynonymSlotAnnotation> synonyms = validateAlleleSynonyms(allele, dto);
		List<AlleleSecondaryIdSlotAnnotation> secondaryIds = validateAlleleSecondaryIds(allele, dto);
		List<AlleleFunctionalImpactSlotAnnotation> functionalImpacts = validateAlleleFunctionalImpacts(allele, dto);

		alleleResponse.convertErrorMessagesToMap();
		
		if (alleleResponse.hasErrors())
			throw new ObjectValidationException(dto, alleleResponse.errorMessagesString());
		
		allele = alleleDAO.persist(allele);

		// Attach allele and persist SlotAnnotation objects

		if (allele.getAlleleMutationTypes() != null)
			allele.getAlleleMutationTypes().clear();
		if (mutationTypes != null) {
			for (AlleleMutationTypeSlotAnnotation mt : mutationTypes) {
				mt.setSingleAllele(allele);
				alleleMutationTypeDAO.persist(mt);
			}
			if (allele.getAlleleMutationTypes() == null)
				allele.setAlleleMutationTypes(new ArrayList<>());
			allele.getAlleleMutationTypes().addAll(mutationTypes);
		}
			
		if (allele.getAlleleInheritanceModes() != null)
			allele.getAlleleInheritanceModes().clear();
		if (inheritanceModes != null) {
			for (AlleleInheritanceModeSlotAnnotation im : inheritanceModes) {
				im.setSingleAllele(allele);
				alleleInheritanceModeDAO.persist(im);
			}
			if (allele.getAlleleInheritanceModes() == null)
				allele.setAlleleInheritanceModes(new ArrayList<>());
			allele.getAlleleInheritanceModes().addAll(inheritanceModes);
		}
		
		if (germlineTransmissionStatus != null) {
			germlineTransmissionStatus.setSingleAllele(allele);
			alleleGermlineTransmissionStatusDAO.persist(germlineTransmissionStatus);
		}
		allele.setAlleleGermlineTransmissionStatus(germlineTransmissionStatus);
		
		if (databaseStatus != null) {
			databaseStatus.setSingleAllele(allele);
			alleleDatabaseStatusDAO.persist(databaseStatus);
		}
		allele.setAlleleDatabaseStatus(databaseStatus);

		if (allele.getAlleleNomenclatureEvents() != null)
			allele.getAlleleNomenclatureEvents().clear();
		if (nomenclatureEvents != null) {
			for (AlleleNomenclatureEventSlotAnnotation ne : nomenclatureEvents) {
				ne.setSingleAllele(allele);
				alleleNomenclatureEventDAO.persist(ne);
			}
			if (allele.getAlleleNomenclatureEvents() == null)
				allele.setAlleleNomenclatureEvents(new ArrayList<>());
			allele.getAlleleNomenclatureEvents().addAll(nomenclatureEvents);
		}
			
		if (symbol != null) {
			symbol.setSingleAllele(allele);
			alleleSymbolDAO.persist(symbol);
		}
		allele.setAlleleSymbol(symbol);

		if (fullName != null) {
			fullName.setSingleAllele(allele);
			alleleFullNameDAO.persist(fullName);
		}
		allele.setAlleleFullName(fullName);

		if (allele.getAlleleSynonyms() != null)
			allele.getAlleleSynonyms().clear();
		if (synonyms != null) {
			for (AlleleSynonymSlotAnnotation syn : synonyms) {
				syn.setSingleAllele(allele);
				alleleSynonymDAO.persist(syn);
			}
			if (allele.getAlleleSynonyms() == null)
				allele.setAlleleSynonyms(new ArrayList<>());
			allele.getAlleleSynonyms().addAll(synonyms);
		}

		if (allele.getAlleleSecondaryIds() != null)
			allele.getAlleleSecondaryIds().clear();
		if (secondaryIds != null) {
			for (AlleleSecondaryIdSlotAnnotation sid : secondaryIds) {
				sid.setSingleAllele(allele);
				alleleSecondaryIdDAO.persist(sid);
			}
			if (allele.getAlleleSecondaryIds() == null)
				allele.setAlleleSecondaryIds(new ArrayList<>());
			allele.getAlleleSecondaryIds().addAll(secondaryIds);
		}

		if (allele.getAlleleFunctionalImpacts() != null)
			allele.getAlleleFunctionalImpacts().clear();
		if (functionalImpacts != null) {
			for (AlleleFunctionalImpactSlotAnnotation fi : functionalImpacts) {
				fi.setSingleAllele(allele);
				alleleFunctionalImpactDAO.persist(fi);
			}
			if (allele.getAlleleFunctionalImpacts() == null)
				allele.setAlleleFunctionalImpacts(new ArrayList<>());
			allele.getAlleleFunctionalImpacts().addAll(functionalImpacts);
		}

		return allele;
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
					alleleResponse.addErrorMessages(field, ix, mtResponse.getErrorMessages());
				} else {
					validatedMutationTypes.add(mtResponse.getEntity());
				}
			}
		}
		
		if (!allValid) {
			alleleResponse.convertMapToErrorMessages(field);
			return null;
		}
		
		if (CollectionUtils.isEmpty(validatedMutationTypes))
			return null;

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
					alleleResponse.addErrorMessages(field, ix, imResponse.getErrorMessages());
				} else {
					validatedInheritanceModes.add(imResponse.getEntity());
				}
			}
		}
		
		if (!allValid) {
			alleleResponse.convertMapToErrorMessages(field);
			return null;
		}
		
		if (CollectionUtils.isEmpty(validatedInheritanceModes))
			return null;

		return validatedInheritanceModes;
	}
	
	private AlleleGermlineTransmissionStatusSlotAnnotation validateAlleleGermlineTransmissionStatus(Allele allele, AlleleDTO dto) {
		if (dto.getAlleleGermlineTransmissionStatusDto() == null)
			return null;
		
		String field = "allele_germline_transmission_status_dto";
		
		ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> agtsResponse = alleleGermlineTransmissionStatusDtoValidator.validateAlleleGermlineTransmissionStatusSlotAnnotationDTO(allele.getAlleleGermlineTransmissionStatus(), dto.getAlleleGermlineTransmissionStatusDto());
		if (agtsResponse.hasErrors()) {
			alleleResponse.addErrorMessage(field, agtsResponse.errorMessagesString());
			alleleResponse.addErrorMessages(field, agtsResponse.getErrorMessages());
			return null;
		}
		
		return agtsResponse.getEntity();
	}
	
	private AlleleDatabaseStatusSlotAnnotation validateAlleleDatabaseStatus(Allele allele, AlleleDTO dto) {
		if (dto.getAlleleDatabaseStatusDto() == null)
			return null;
		
		String field = "allele_database_status_dto";
		
		ObjectResponse<AlleleDatabaseStatusSlotAnnotation> adsResponse = alleleDatabaseStatusDtoValidator.validateAlleleDatabaseStatusSlotAnnotationDTO(allele.getAlleleDatabaseStatus(), dto.getAlleleDatabaseStatusDto());
		if (adsResponse.hasErrors()) {
			alleleResponse.addErrorMessage(field, adsResponse.errorMessagesString());
			alleleResponse.addErrorMessages(field, adsResponse.getErrorMessages());
			return null;
		}
		
		return adsResponse.getEntity();
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
			for (int ix = 0; ix < dto.getAlleleInheritanceModeDtos().size(); ix++) {
				AlleleNomenclatureEventSlotAnnotationDTO neDto = dto.getAlleleNomenclatureEventDtos().get(ix);
				AlleleNomenclatureEventSlotAnnotation ne = existingNomenclatureEvents.remove(identityHelper.alleleNomenclatureEventDtoIdentity(neDto));
				ObjectResponse<AlleleNomenclatureEventSlotAnnotation> neResponse = alleleNomenclatureEventDtoValidator.validateAlleleNomenclatureEventSlotAnnotationDTO(ne, neDto);
				if (neResponse.hasErrors()) {
					allValid = false;
					alleleResponse.addErrorMessages(field, ix, neResponse.getErrorMessages());
				} else {
					validatedNomenclatureEvents.add(neResponse.getEntity());
				}
			}
		}
		
		if (!allValid) {
			alleleResponse.convertMapToErrorMessages(field);
			return null;
		}
		
		if (CollectionUtils.isEmpty(validatedNomenclatureEvents))
			return null;

		return validatedNomenclatureEvents;
	}

	private AlleleSymbolSlotAnnotation validateAlleleSymbol(Allele allele, AlleleDTO dto) {
		String field = "allele_symbol_dto";

		if (dto.getAlleleSymbolDto() == null) {
			alleleResponse.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<AlleleSymbolSlotAnnotation> symbolResponse = alleleSymbolDtoValidator.validateAlleleSymbolSlotAnnotationDTO(allele.getAlleleSymbol(), dto.getAlleleSymbolDto());
		if (symbolResponse.hasErrors()) {
			alleleResponse.addErrorMessage(field, symbolResponse.errorMessagesString());
			alleleResponse.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		return symbolResponse.getEntity();
	}

	private AlleleFullNameSlotAnnotation validateAlleleFullName(Allele allele, AlleleDTO dto) {
		if (dto.getAlleleFullNameDto() == null)
			return null;

		String field = "allele_full_name_dto";

		ObjectResponse<AlleleFullNameSlotAnnotation> nameResponse = alleleFullNameDtoValidator.validateAlleleFullNameSlotAnnotationDTO(allele.getAlleleFullName(), dto.getAlleleFullNameDto());
		if (nameResponse.hasErrors()) {
			alleleResponse.addErrorMessage(field, nameResponse.errorMessagesString());
			alleleResponse.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		return nameResponse.getEntity();
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
					alleleResponse.addErrorMessages(field, ix, synResponse.getErrorMessages());
				} else {
					validatedSynonyms.add(synResponse.getEntity());
				}
			}
		}
		
		if (!allValid) {
			alleleResponse.convertMapToErrorMessages(field);
			return null;
		}
		
		if (CollectionUtils.isEmpty(validatedSynonyms))
			return null;

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
					alleleResponse.addErrorMessages(field, ix, sidResponse.getErrorMessages());
				} else {
					validatedSecondaryIds.add(sidResponse.getEntity());
				}
			}
		}
		
		if (!allValid) {
			alleleResponse.convertMapToErrorMessages(field);
			return null;
		}
		
		if (CollectionUtils.isEmpty(validatedSecondaryIds))
			return null;

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
					alleleResponse.addErrorMessages(field, ix, fiResponse.getErrorMessages());
				} else {
					validatedFunctionalImpacts.add(fiResponse.getEntity());
				}
			}
		}
		
		if (!allValid) {
			alleleResponse.convertMapToErrorMessages(field);
			return null;
		}
		
		if (CollectionUtils.isEmpty(validatedFunctionalImpacts))
			return null;

		return validatedFunctionalImpacts;
	}
	
	private List<Note> validateRelatedNotes(Allele allele, AlleleDTO dto) {
		String field = "relatedNotes";
	
		if (allele.getRelatedNotes() != null)
			allele.getRelatedNotes().clear();
		
		List<Note> validatedNotes = new ArrayList<Note>();
		List<String> noteIdentities = new ArrayList<String>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getNoteDtos())) {
			for (int ix = 0; ix < dto.getNoteDtos().size(); ix++) {
				ObjectResponse<Note> noteResponse = noteDtoValidator.validateNoteDTO(dto.getNoteDtos().get(ix), VocabularyConstants.ALLELE_NOTE_TYPES_VOCABULARY_TERM_SET);
				if (noteResponse.hasErrors()) {
					allValid = false;
					alleleResponse.addErrorMessages(field, ix, noteResponse.getErrorMessages());
				} else {
					String noteIdentity = NoteIdentityHelper.noteDtoIdentity(dto.getNoteDtos().get(ix));
					if (!noteIdentities.contains(noteIdentity)) {
						noteIdentities.add(noteIdentity);
						validatedNotes.add(noteDAO.persist(noteResponse.getEntity()));
					}
				}
			}
		}
		
		if (!allValid) {
			alleleResponse.convertMapToErrorMessages(field);
			return null;
		}
		
		if (CollectionUtils.isEmpty(validatedNotes))
			return null;
		
		return validatedNotes;
	}

}
