package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class AlleleValidator extends GenomicEntityValidator {
	
	@Inject AlleleDAO alleleDAO;
	@Inject AlleleMutationTypeSlotAnnotationDAO alleleMutationTypeDAO;
	@Inject AlleleSymbolSlotAnnotationDAO alleleSymbolDAO;
	@Inject AlleleFullNameSlotAnnotationDAO alleleFullNameDAO;
	@Inject AlleleSynonymSlotAnnotationDAO alleleSynonymDAO;
	@Inject AlleleMutationTypeSlotAnnotationValidator alleleMutationTypeValidator;
	@Inject AlleleSymbolSlotAnnotationValidator alleleSymbolValidator;
	@Inject AlleleFullNameSlotAnnotationValidator alleleFullNameValidator;
	@Inject AlleleSynonymSlotAnnotationValidator alleleSynonymValidator;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject ReferenceValidator referenceValidator;
	
	private String errorMessage;
	
	public Allele validateAlleleUpdate(Allele uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Allele: [" + uiEntity.getCurie() + "]";
		
		String curie = validateCurie(uiEntity);
		if (curie == null) {
			throw new ApiErrorException(response);
		}
		
		Allele dbEntity = alleleDAO.find(curie);
		if (dbEntity == null) {
			addMessageResponse("curie", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}

		dbEntity = (Allele) validateAuditedObjectFields(uiEntity, dbEntity, false);
		
		return validateAllele(uiEntity, dbEntity);
	}
	
	public Allele validateAlleleCreate(Allele uiEntity) {
		response = new ObjectResponse<>();
		errorMessage = "Could not create Allele: [" + uiEntity.getCurie() + "]";
		
		Allele dbEntity = new Allele();
		String curie = validateCurie(uiEntity);
		dbEntity.setCurie(curie);
		
		dbEntity = (Allele) validateAuditedObjectFields(uiEntity, dbEntity, true);
		
		return validateAllele(uiEntity, dbEntity);
	}		

	public Allele validateAllele(Allele uiEntity, Allele dbEntity) {

		NCBITaxonTerm taxon = validateTaxon(uiEntity);
		dbEntity.setTaxon(taxon);
		
		List<String> previousReferenceCuries = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(dbEntity.getReferences()))
			previousReferenceCuries = dbEntity.getReferences().stream().map(Reference::getCurie).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(uiEntity.getReferences())) {
			List<Reference> references = new ArrayList<Reference>();
			for (Reference uiReference : uiEntity.getReferences()) {
				Reference reference = validateReference(uiReference, previousReferenceCuries);
				if (reference != null) {
					references.add(reference);
				}
			}
			dbEntity.setReferences(references);
		} else {
			dbEntity.setReferences(null);
		}

		VocabularyTerm inheritanceMode = validateInheritanceMode(uiEntity, dbEntity);
		dbEntity.setInheritanceMode(inheritanceMode);

		VocabularyTerm inCollection = validateInCollection(uiEntity, dbEntity);
		dbEntity.setInCollection(inCollection);

		if(uiEntity.getIsExtinct() != null) {
			dbEntity.setIsExtinct(uiEntity.getIsExtinct());
		}else{
			dbEntity.setIsExtinct(null);
		}
		
		List<CrossReference> crossReferences = validateCrossReferences(uiEntity, dbEntity);
		dbEntity.setCrossReferences(crossReferences);
		
		if (CollectionUtils.isNotEmpty(uiEntity.getSecondaryIdentifiers())) {
			dbEntity.setSecondaryIdentifiers(uiEntity.getSecondaryIdentifiers());
		} else {
			dbEntity.setSecondaryIdentifiers(null);
		}
		
		removeUnusedSlotAnnotations(uiEntity, dbEntity);
		
		List<AlleleMutationTypeSlotAnnotation> mutationTypes = validateAlleleMutationTypes(uiEntity, dbEntity);
		
		AlleleSymbolSlotAnnotation symbol = validateAlleleSymbol(uiEntity, dbEntity);
		AlleleFullNameSlotAnnotation fullName = validateAlleleFullName(uiEntity, dbEntity);
		List<AlleleSynonymSlotAnnotation> synonyms = validateAlleleSynonyms(uiEntity, dbEntity);
		
		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}
		
		dbEntity = alleleDAO.persist(dbEntity);
		
		if (mutationTypes != null) {
			for (AlleleMutationTypeSlotAnnotation mt : mutationTypes) {
				mt.setSingleAllele(dbEntity);
				alleleMutationTypeDAO.persist(mt);
			}
		}
		dbEntity.setAlleleMutationTypes(mutationTypes);
		
		if (symbol != null) {
			symbol.setSingleAllele(dbEntity);
			alleleSymbolDAO.persist(symbol);
		}
		dbEntity.setAlleleSymbol(symbol);
		
		if (fullName != null) {
			fullName.setSingleAllele(dbEntity);
			alleleFullNameDAO.persist(fullName);
		}
		dbEntity.setAlleleFullName(fullName);
		
		if (synonyms != null) {
			for (AlleleSynonymSlotAnnotation syn : synonyms) {
				syn.setSingleAllele(dbEntity);
				alleleSynonymDAO.persist(syn);
			}
		}
		dbEntity.setAlleleSynonyms(synonyms);
		
		return dbEntity;
	}
	
	private Reference validateReference (Reference uiEntity, List<String> previousCuries) {
		ObjectResponse<Reference> singleRefResponse = referenceValidator.validateReference(uiEntity);
		if (singleRefResponse.getEntity() == null) {
			Map<String, String> errors = singleRefResponse.getErrorMessages();
			for (String refField : errors.keySet()) {
				addMessageResponse("references", refField + " - " + errors.get(refField));
			}
			return null;
		}
		
		if (singleRefResponse.getEntity().getObsolete() && !previousCuries.contains(singleRefResponse.getEntity().getCurie())) {
			addMessageResponse("references", "curie - " + ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		
		return singleRefResponse.getEntity();
	}

	private VocabularyTerm validateInheritanceMode(Allele uiEntity, Allele dbEntity) {
		String field = "inheritanceMode";

		if (uiEntity.getInheritanceMode() == null)
			return null;
		
		VocabularyTerm inheritanceMode = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY, uiEntity.getInheritanceMode().getName());
		if (inheritanceMode == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (inheritanceMode.getObsolete() && (dbEntity.getInheritanceMode() == null || !inheritanceMode.getName().equals(dbEntity.getInheritanceMode().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return inheritanceMode;
	}

	private VocabularyTerm validateInCollection(Allele uiEntity, Allele dbEntity) {
		String field = "inCollection";

		if (uiEntity.getInCollection() == null)
			return null;

		VocabularyTerm inCollection = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.ALLELE_COLLECTION_VOCABULARY, uiEntity.getInCollection().getName());
		if (inCollection == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (inCollection.getObsolete() && (dbEntity.getInCollection() == null || !inCollection.getName().equals(dbEntity.getInCollection().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return inCollection;
	}

	private void removeUnusedAlleleMutationTypes (Allele uiEntity, Allele dbEntity) {
		List<Long> reusedIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleMutationTypes()))
			reusedIds = uiEntity.getAlleleMutationTypes().stream().map(AlleleMutationTypeSlotAnnotation::getId).collect(Collectors.toList());
		
		if (CollectionUtils.isNotEmpty(dbEntity.getAlleleMutationTypes())) {	
			for (AlleleMutationTypeSlotAnnotation previousMutationType : dbEntity.getAlleleMutationTypes()) {
				if (!reusedIds.contains(previousMutationType.getId())) {
					previousMutationType.setSingleAllele(null);
					alleleMutationTypeDAO.remove(previousMutationType.getId());
				}
			}
		}
	}

	private void removeUnusedAlleleSymbol (Allele uiEntity, Allele dbEntity) {
		Long reusedId = uiEntity.getAlleleSymbol().getId();
		AlleleSymbolSlotAnnotation previousSymbol = dbEntity.getAlleleSymbol();
		
		if (previousSymbol != null && reusedId != null && !previousSymbol.getId().equals(reusedId)) {
			previousSymbol.setSingleAllele(null);
			alleleSymbolDAO.remove(previousSymbol.getId());
		}
	}

	private void removeUnusedAlleleFullName (Allele uiEntity, Allele dbEntity) {
		Long reusedId = uiEntity.getAlleleFullName().getId();
		AlleleFullNameSlotAnnotation previousFullName = dbEntity.getAlleleFullName();
		
		if (previousFullName != null && reusedId != null && !previousFullName.getId().equals(reusedId)) {
			previousFullName.setSingleAllele(null);
			alleleFullNameDAO.remove(previousFullName.getId());
		}
	}

	private void removeUnusedAlleleSynonyms (Allele uiEntity, Allele dbEntity) {
		List<Long> reusedIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleSynonyms()))
			reusedIds = uiEntity.getAlleleSynonyms().stream().map(AlleleSynonymSlotAnnotation::getId).collect(Collectors.toList());
		
		if (CollectionUtils.isNotEmpty(dbEntity.getAlleleSynonyms())) {	
			for (AlleleSynonymSlotAnnotation previousSynonym : dbEntity.getAlleleSynonyms()) {
				if (!reusedIds.contains(previousSynonym.getId())) {
					previousSynonym.setSingleAllele(null);
					alleleSynonymDAO.remove(previousSynonym.getId());
				}
			}
		}
	}
	
	private List<AlleleMutationTypeSlotAnnotation> validateAlleleMutationTypes (Allele uiEntity, Allele dbEntity) {
		String field = "alleleMutationTypes";
		
		List<AlleleMutationTypeSlotAnnotation> validatedMutationTypes = new ArrayList<AlleleMutationTypeSlotAnnotation>();
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleMutationTypes())) {
			for (AlleleMutationTypeSlotAnnotation mt : uiEntity.getAlleleMutationTypes()) {
				ObjectResponse<AlleleMutationTypeSlotAnnotation> mtResponse = alleleMutationTypeValidator.validateAlleleMutationTypeSlotAnnotation(mt);
				if (mtResponse.getEntity() == null) {
					Map<String, String> errors = mtResponse.getErrorMessages();
					for (String mtField : errors.keySet()) {
						addMessageResponse(field, mtField + " - " + errors.get(mtField));
					}
					return null;
				}
				mt = mtResponse.getEntity();
				validatedMutationTypes.add(mt);
			}
		}
		
		if (CollectionUtils.isEmpty(validatedMutationTypes))
			return null;

		return validatedMutationTypes;
	}
	
	private AlleleSymbolSlotAnnotation validateAlleleSymbol (Allele uiEntity, Allele dbEntity) {
		String field = "alleleSymbol";
		
		ObjectResponse<AlleleSymbolSlotAnnotation> symbolResponse = alleleSymbolValidator.validateAlleleSymbolSlotAnnotation(uiEntity.getAlleleSymbol());
		if (symbolResponse.getEntity() == null) {
			Map<String, String> errors = symbolResponse.getErrorMessages();
			for (String symbolField : errors.keySet()) {
				addMessageResponse(field, symbolField + " - " + errors.get(symbolField));
			}
			return null;
		}
		
		return symbolResponse.getEntity();
	}
	
	private AlleleFullNameSlotAnnotation validateAlleleFullName (Allele uiEntity, Allele dbEntity) {
		String field = "alleleFullName";
		
		ObjectResponse<AlleleFullNameSlotAnnotation> nameResponse = alleleFullNameValidator.validateAlleleFullNameSlotAnnotation(uiEntity.getAlleleFullName());
		if (nameResponse.getEntity() == null) {
			Map<String, String> errors = nameResponse.getErrorMessages();
			for (String nameField : errors.keySet()) {
				addMessageResponse(field, nameField + " - " + errors.get(nameField));
			}
			return null;
		}
		
		return nameResponse.getEntity();
	}
	
	private List<AlleleSynonymSlotAnnotation> validateAlleleSynonyms (Allele uiEntity, Allele dbEntity) {
		String field = "alleleSynonyms";
		
		List<AlleleSynonymSlotAnnotation> validatedSynonyms = new ArrayList<AlleleSynonymSlotAnnotation>();
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleSynonyms())) {
			for (AlleleSynonymSlotAnnotation syn : uiEntity.getAlleleSynonyms()) {
				ObjectResponse<AlleleSynonymSlotAnnotation> synResponse = alleleSynonymValidator.validateAlleleSynonymSlotAnnotation(syn);
				if (synResponse.getEntity() == null) {
					Map<String, String> errors = synResponse.getErrorMessages();
					for (String synField : errors.keySet()) {
						addMessageResponse(field, synField + " - " + errors.get(synField));
					}
					return null;
				}
				syn = synResponse.getEntity();
				validatedSynonyms.add(syn);
			}
		}
		
		if (CollectionUtils.isEmpty(validatedSynonyms))
			return null;

		return validatedSynonyms;
	}
	
	private void removeUnusedSlotAnnotations(Allele uiEntity, Allele dbEntity) {
		if (CollectionUtils.isNotEmpty(dbEntity.getAlleleMutationTypes()))
			removeUnusedAlleleMutationTypes(uiEntity, dbEntity);
		
		removeUnusedAlleleSymbol(uiEntity, dbEntity);
		
		removeUnusedAlleleFullName(uiEntity, dbEntity);
		
		if (CollectionUtils.isNotEmpty(dbEntity.getAlleleSynonyms()))
			removeUnusedAlleleSynonyms(uiEntity, dbEntity);
	}
}
