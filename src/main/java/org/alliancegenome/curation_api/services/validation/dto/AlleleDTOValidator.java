package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleMutationTypeSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.SlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleDTOValidator extends BaseDTOValidator {

	@Inject AlleleDAO alleleDAO;
	@Inject AlleleMutationTypeSlotAnnotationDAO alleleMutationTypeDAO;
	@Inject AlleleSymbolSlotAnnotationDAO alleleSymbolDAO;
	@Inject AlleleFullNameSlotAnnotationDAO alleleFullNameDAO;
	@Inject AlleleSynonymSlotAnnotationDAO alleleSynonymDAO;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceService referenceService;
	@Inject AlleleMutationTypeSlotAnnotationDTOValidator alleleMutationTypeDtoValidator;
	@Inject AlleleSymbolSlotAnnotationDTOValidator alleleSymbolDtoValidator;
	@Inject AlleleFullNameSlotAnnotationDTOValidator alleleFullNameDtoValidator;
	@Inject AlleleSynonymSlotAnnotationDTOValidator alleleSynonymDtoValidator;

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
		
		VocabularyTerm inheritenceMode = null;
		if (StringUtils.isNotBlank(dto.getInheritanceModeName())) {
			inheritenceMode = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY, dto.getInheritanceModeName());
			if (inheritenceMode == null) {
				alleleResponse.addErrorMessage("inheritance_mode_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getInheritanceModeName() + ")");
			}
		}
		allele.setInheritanceMode(inheritenceMode);
		
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
				alleleMutationTypeDAO.remove(amt.getId());});
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
		
		AlleleSymbolSlotAnnotation symbol = null;
		if (dto.getAlleleSymbolDto() == null) {
			alleleResponse.addErrorMessage("allele_symbol_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<AlleleSymbolSlotAnnotation> symbolResponse = alleleSymbolDtoValidator.validateAlleleSymbolSlotAnnotationDTO(dto.getAlleleSymbolDto());
			if (symbolResponse.hasErrors()) {
				alleleResponse.addErrorMessage("allele_symbol_dto", symbolResponse.errorMessagesString());
			} else {
				symbol = symbolResponse.getEntity();
			}
		}
		
		AlleleFullNameSlotAnnotation fullName = null;
		if (dto.getAlleleFullNameDto() != null) {
			ObjectResponse<AlleleFullNameSlotAnnotation> fullNameResponse = alleleFullNameDtoValidator.validateAlleleFullNameSlotAnnotationDTO(dto.getAlleleFullNameDto());
			if (fullNameResponse.hasErrors()) {
				alleleResponse.addErrorMessage("allele_full_name_dto", fullNameResponse.errorMessagesString());
			} else {
				fullName = fullNameResponse.getEntity();
			}
		}
		
		List<AlleleSynonymSlotAnnotation> synonyms = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getAlleleSynonymDtos())) {
			for (NameSlotAnnotationDTO synonymDTO : dto.getAlleleSynonymDtos()) {
				ObjectResponse<AlleleSynonymSlotAnnotation> synonymResponse = alleleSynonymDtoValidator.validateAlleleSynonymSlotAnnotationDTO(synonymDTO);
				if (synonymResponse.hasErrors()) {
					alleleResponse.addErrorMessage("allele_synonym_dtos", synonymResponse.errorMessagesString());
				} else {
					AlleleSynonymSlotAnnotation synonym = synonymResponse.getEntity();
					synonyms.add(synonym);
				}
			}
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
		
		return allele;
	}

}
