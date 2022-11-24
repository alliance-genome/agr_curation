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
import org.alliancegenome.curation_api.dao.slotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleMutationTypeSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AlleleMutationTypeSlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleDTOValidator extends BaseDTOValidator {

	@Inject AlleleDAO alleleDAO;
	@Inject AlleleMutationTypeSlotAnnotationDAO alleleMutationTypeDAO;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceService referenceService;
	@Inject AlleleMutationTypeSlotAnnotationDTOValidator alleleMutationTypeDtoValidator;

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
		
		if (StringUtils.isBlank(dto.getSymbol()))
			alleleResponse.addErrorMessage("symbol", ValidationConstants.REQUIRED_MESSAGE);
		allele.setSymbol(dto.getSymbol());
				
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
					mutationType.setSingleAllele(allele);
					mutationTypes.add(mutationType);
				}
			}
		}
		
		if (alleleResponse.hasErrors()) {
			throw new ObjectValidationException(dto, alleleResponse.errorMessagesString());
		} 
		
		allele = alleleDAO.persist(allele);
		
		if (CollectionUtils.isNotEmpty(mutationTypes)) {
			for (AlleleMutationTypeSlotAnnotation mt : mutationTypes) {
				mt.setSingleAllele(allele);
				alleleMutationTypeDAO.persist(mt);
			}
		}
		
		return allele;
	}

}
