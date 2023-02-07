package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleInheritanceModeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleMutationTypeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleSecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleDTOValidator extends BaseDTOValidator {

	@Inject
	AlleleDAO alleleDAO;
	@Inject
	AlleleMutationTypeSlotAnnotationDAO alleleMutationTypeDAO;
	@Inject
	AlleleInheritanceModeSlotAnnotationDAO alleleInheritanceModeDAO;
	@Inject
	AlleleSymbolSlotAnnotationDAO alleleSymbolDAO;
	@Inject
	AlleleFullNameSlotAnnotationDAO alleleFullNameDAO;
	@Inject
	AlleleSynonymSlotAnnotationDAO alleleSynonymDAO;
	@Inject
	AlleleSecondaryIdSlotAnnotationDAO alleleSecondaryIdDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	ReferenceDAO referenceDAO;
	@Inject
	ReferenceService referenceService;
	@Inject
	AlleleMutationTypeSlotAnnotationDTOValidator alleleMutationTypeDtoValidator;
	@Inject
	AlleleInheritanceModeSlotAnnotationDTOValidator alleleInheritanceModeDtoValidator;
	@Inject
	AlleleSymbolSlotAnnotationDTOValidator alleleSymbolDtoValidator;
	@Inject
	AlleleFullNameSlotAnnotationDTOValidator alleleFullNameDtoValidator;
	@Inject
	AlleleSynonymSlotAnnotationDTOValidator alleleSynonymDtoValidator;
	@Inject
	AlleleSecondaryIdSlotAnnotationDTOValidator alleleSecondaryIdDtoValidator;

	@Transactional
	public Allele validateAlleleDTO(AlleleDTO dto) throws ObjectValidationException {

		ObjectResponse<Allele> alleleResponse = new ObjectResponse<Allele>();

		Allele allele = null;
		if (StringUtils.isBlank(dto.getCurie())) {
			alleleResponse.addErrorMessage("curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			allele = alleleDAO.find(dto.getCurie());
		}

		if (allele == null)
			allele = new Allele();

		allele.setCurie(dto.getCurie());

		ObjectResponse<Allele> geResponse = validateGenomicEntityDTO(allele, dto);
		alleleResponse.addErrorMessages(geResponse.getErrorMessages());

		allele = geResponse.getEntity();

		VocabularyTerm inCollection = null;
		if (StringUtils.isNotBlank(dto.getInCollectionName())) {
			inCollection = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.ALLELE_COLLECTION_VOCABULARY, dto.getInCollectionName());
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

		if (CollectionUtils.isNotEmpty(allele.getAlleleMutationTypes())) {
			allele.getAlleleMutationTypes().forEach(amt -> {
				amt.setSingleAllele(null);
				alleleMutationTypeDAO.remove(amt.getId());
			});
		}

		List<AlleleMutationTypeSlotAnnotation> mutationTypes = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getAlleleMutationTypeDtos())) {
			for (AlleleMutationTypeSlotAnnotationDTO mutationTypeDTO : dto.getAlleleMutationTypeDtos()) {
				ObjectResponse<AlleleMutationTypeSlotAnnotation> mutationTypeResponse = alleleMutationTypeDtoValidator.validateAlleleMutationTypeSlotAnnotationDTO(mutationTypeDTO);
				if (mutationTypeResponse.hasErrors()) {
					alleleResponse.addErrorMessage("allele_mutation_type_dtos", mutationTypeResponse.errorMessagesString());
				} else {
					AlleleMutationTypeSlotAnnotation mutationType = mutationTypeResponse.getEntity();
					mutationTypes.add(mutationType);
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(allele.getAlleleInheritanceModes())) {
			allele.getAlleleInheritanceModes().forEach(aim -> {
				aim.setSingleAllele(null);
				alleleInheritanceModeDAO.remove(aim.getId());
			});
		}

		List<AlleleInheritanceModeSlotAnnotation> inheritanceModes = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getAlleleInheritanceModeDtos())) {
			for (AlleleInheritanceModeSlotAnnotationDTO inheritanceModeDTO : dto.getAlleleInheritanceModeDtos()) {
				ObjectResponse<AlleleInheritanceModeSlotAnnotation> inheritanceModeResponse = alleleInheritanceModeDtoValidator.validateAlleleInheritanceModeSlotAnnotationDTO(inheritanceModeDTO);
				if (inheritanceModeResponse.hasErrors()) {
					alleleResponse.addErrorMessage("allele_inheritance_mode_dtos", inheritanceModeResponse.errorMessagesString());
				} else {
					AlleleInheritanceModeSlotAnnotation inheritanceMode = inheritanceModeResponse.getEntity();
					inheritanceModes.add(inheritanceMode);
				}
			}
		}

		AlleleSymbolSlotAnnotation symbol = allele.getAlleleSymbol();
		if (dto.getAlleleSymbolDto() == null) {
			alleleResponse.addErrorMessage("allele_symbol_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<AlleleSymbolSlotAnnotation> symbolResponse = alleleSymbolDtoValidator.validateAlleleSymbolSlotAnnotationDTO(symbol, dto.getAlleleSymbolDto());
			if (symbolResponse.hasErrors()) {
				alleleResponse.addErrorMessage("allele_symbol_dto", symbolResponse.errorMessagesString());
			} else {
				symbol = symbolResponse.getEntity();
			}
		}

		AlleleFullNameSlotAnnotation fullName = allele.getAlleleFullName();
		if (allele.getAlleleFullName() != null && dto.getAlleleFullNameDto() == null) {
			fullName.setSingleAllele(null);
			alleleFullNameDAO.remove(fullName.getId());
		}

		if (dto.getAlleleFullNameDto() != null) {
			ObjectResponse<AlleleFullNameSlotAnnotation> fullNameResponse = alleleFullNameDtoValidator.validateAlleleFullNameSlotAnnotationDTO(fullName, dto.getAlleleFullNameDto());
			if (fullNameResponse.hasErrors()) {
				alleleResponse.addErrorMessage("allele_full_name_dto", fullNameResponse.errorMessagesString());
			} else {
				fullName = fullNameResponse.getEntity();
			}
		} else {
			fullName = null;
		}

		Map<String, AlleleSynonymSlotAnnotation> existingSynonyms = new HashMap<>();
		if (CollectionUtils.isNotEmpty(allele.getAlleleSynonyms()))
			existingSynonyms = allele.getAlleleSynonyms().stream().collect(Collectors.toMap(AlleleSynonymSlotAnnotation::getFormatText, Function.identity()));
		
		List<AlleleSynonymSlotAnnotation> synonyms = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getAlleleSynonymDtos())) {
			for (NameSlotAnnotationDTO synonymDTO : dto.getAlleleSynonymDtos()) {
				ObjectResponse<AlleleSynonymSlotAnnotation> synonymResponse = alleleSynonymDtoValidator.validateAlleleSynonymSlotAnnotationDTO(existingSynonyms.get(synonymDTO.getFormatText()), synonymDTO);
				if (synonymResponse.hasErrors()) {
					alleleResponse.addErrorMessage("allele_synonym_dtos", synonymResponse.errorMessagesString());
				} else {
					AlleleSynonymSlotAnnotation synonym = synonymResponse.getEntity();
					synonyms.add(synonym);
				}
			}
		}
		
		if (!existingSynonyms.isEmpty()) {
			List<String> synonymNames = synonyms.stream().map(AlleleSynonymSlotAnnotation::getFormatText).collect(Collectors.toList());
			existingSynonyms.forEach((k,v) -> {
				if (!synonymNames.contains(k)) {
					v.setSingleAllele(null);
					alleleSynonymDAO.remove(v.getId());
				}
			});
		}

		Map<String, AlleleSecondaryIdSlotAnnotation> existingSecondaryIds = new HashMap<>();
		if (CollectionUtils.isNotEmpty(allele.getAlleleSecondaryIds()))
			existingSecondaryIds = allele.getAlleleSecondaryIds().stream().collect(Collectors.toMap(AlleleSecondaryIdSlotAnnotation::getSecondaryId, Function.identity()));
		
		List<AlleleSecondaryIdSlotAnnotation> secondaryIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getAlleleSecondaryIdDtos())) {
			for (AlleleSecondaryIdSlotAnnotationDTO secondaryIdDTO : dto.getAlleleSecondaryIdDtos()) {
				ObjectResponse<AlleleSecondaryIdSlotAnnotation> secondaryIdResponse = alleleSecondaryIdDtoValidator.validateAlleleSecondaryIdSlotAnnotationDTO(existingSecondaryIds.get(secondaryIdDTO.getSecondaryId()), secondaryIdDTO);
				if (secondaryIdResponse.hasErrors()) {
					alleleResponse.addErrorMessage("allele_secondary_id_dtos", secondaryIdResponse.errorMessagesString());
				} else {
					AlleleSecondaryIdSlotAnnotation secondaryId = secondaryIdResponse.getEntity();
					secondaryIds.add(secondaryId);
				}
			}
		}
		
		if (!existingSecondaryIds.isEmpty()) {
			List<String> secondaryIdNames = secondaryIds.stream().map(AlleleSecondaryIdSlotAnnotation::getSecondaryId).collect(Collectors.toList());
			existingSecondaryIds.forEach((k,v) -> {
				if (!secondaryIdNames.contains(k)) {
					v.setSingleAllele(null);
					alleleSecondaryIdDAO.remove(v.getId());
				}
			});
		}

		if (alleleResponse.hasErrors()) {
			throw new ObjectValidationException(dto, alleleResponse.errorMessagesString());
		}

		allele = alleleDAO.persist(allele);

		// Attach allele and persist SlotAnnotation objects

		if (CollectionUtils.isNotEmpty(mutationTypes)) {
			for (AlleleMutationTypeSlotAnnotation mt : mutationTypes) {
				mt.setSingleAllele(allele);
				alleleMutationTypeDAO.persist(mt);
			}
		}
		
		if (CollectionUtils.isNotEmpty(inheritanceModes)) {
			for (AlleleInheritanceModeSlotAnnotation im : inheritanceModes) {
				im.setSingleAllele(allele);
				alleleInheritanceModeDAO.persist(im);
			}
		}

		if (symbol != null) {
			symbol.setSingleAllele(allele);
			alleleSymbolDAO.persist(symbol);
		}

		if (fullName != null) {
			fullName.setSingleAllele(allele);
			alleleFullNameDAO.persist(fullName);
		}

		if (CollectionUtils.isNotEmpty(synonyms)) {
			for (AlleleSynonymSlotAnnotation syn : synonyms) {
				syn.setSingleAllele(allele);
				alleleSynonymDAO.persist(syn);
			}
		}

		if (CollectionUtils.isNotEmpty(secondaryIds)) {
			for (AlleleSecondaryIdSlotAnnotation sid : secondaryIds) {
				sid.setSingleAllele(allele);
				alleleSecondaryIdDAO.persist(sid);
			}
		}

		return allele;
	}

}
