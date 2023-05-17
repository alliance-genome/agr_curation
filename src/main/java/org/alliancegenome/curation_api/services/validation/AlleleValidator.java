package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class AlleleValidator extends GenomicEntityValidator {

	@Inject
	AlleleDAO alleleDAO;
	@Inject
	AlleleMutationTypeSlotAnnotationDAO alleleMutationTypeDAO;
	@Inject
	AlleleInheritanceModeSlotAnnotationDAO alleleInheritanceModeDAO;
	@Inject
	AlleleGermlineTransmissionStatusSlotAnnotationDAO alleleGermlineTransmissionStatusDAO;
	@Inject
	AlleleSymbolSlotAnnotationDAO alleleSymbolDAO;
	@Inject
	AlleleFullNameSlotAnnotationDAO alleleFullNameDAO;
	@Inject
	AlleleSynonymSlotAnnotationDAO alleleSynonymDAO;
	@Inject
	AlleleSecondaryIdSlotAnnotationDAO alleleSecondaryIdDAO;
	@Inject
	AlleleMutationTypeSlotAnnotationValidator alleleMutationTypeValidator;
	@Inject
	AlleleInheritanceModeSlotAnnotationValidator alleleInheritanceModeValidator;
	@Inject
	AlleleGermlineTransmissionStatusSlotAnnotationValidator alleleGermlineTransmissionStatusValidator;
	@Inject
	AlleleSymbolSlotAnnotationValidator alleleSymbolValidator;
	@Inject
	AlleleFullNameSlotAnnotationValidator alleleFullNameValidator;
	@Inject
	AlleleSynonymSlotAnnotationValidator alleleSynonymValidator;
	@Inject
	AlleleSecondaryIdSlotAnnotationValidator alleleSecondaryIdValidator;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	ReferenceValidator referenceValidator;
	@Inject
	CrossReferenceDAO crossReferenceDAO;

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

		NCBITaxonTerm taxon = validateTaxon(uiEntity, dbEntity);
		dbEntity.setTaxon(taxon);
		
		DataProvider dataProvider = validateDataProvider(uiEntity, dbEntity);
		dbEntity.setDataProvider(dataProvider);

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

		VocabularyTerm inCollection = validateInCollection(uiEntity, dbEntity);
		dbEntity.setInCollection(inCollection);

		if (uiEntity.getIsExtinct() != null) {
			dbEntity.setIsExtinct(uiEntity.getIsExtinct());
		} else {
			dbEntity.setIsExtinct(null);
		}

		List<Long> currentXrefIds;
		if (dbEntity.getCrossReferences() == null) {
			currentXrefIds = new ArrayList<>();
		} else {
			currentXrefIds = dbEntity.getCrossReferences().stream().map(CrossReference::getId).collect(Collectors.toList());
		}
		
		List<CrossReference> crossReferences = validateCrossReferences(uiEntity, dbEntity);
		dbEntity.setCrossReferences(crossReferences);
		List<Long> mergedIds = crossReferences == null ? new ArrayList<>() :
			crossReferences.stream().map(CrossReference::getId).collect(Collectors.toList());
		for (Long currentXrefId : currentXrefIds) {
			if (!mergedIds.contains(currentXrefId)) {
				crossReferenceDAO.remove(currentXrefId);
			}
		}

		removeUnusedSlotAnnotations(uiEntity, dbEntity);

		List<AlleleMutationTypeSlotAnnotation> mutationTypes = validateAlleleMutationTypes(uiEntity, dbEntity);
		
		List<AlleleInheritanceModeSlotAnnotation> inheritanceModes = validateAlleleInheritanceModes(uiEntity, dbEntity);
		
		AlleleGermlineTransmissionStatusSlotAnnotation germlineTransmissionStatus = validateAlleleGermlineTransmissionStatus(uiEntity, dbEntity);

		AlleleSymbolSlotAnnotation symbol = validateAlleleSymbol(uiEntity, dbEntity);
		AlleleFullNameSlotAnnotation fullName = validateAlleleFullName(uiEntity, dbEntity);
		List<AlleleSynonymSlotAnnotation> synonyms = validateAlleleSynonyms(uiEntity, dbEntity);
		
		List<AlleleSecondaryIdSlotAnnotation> secondaryIds = validateAlleleSecondaryIds(uiEntity, dbEntity);

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

		if (inheritanceModes != null) {
			for (AlleleInheritanceModeSlotAnnotation im : inheritanceModes) {
				im.setSingleAllele(dbEntity);
				alleleInheritanceModeDAO.persist(im);
			}
		}
		dbEntity.setAlleleInheritanceModes(inheritanceModes);
		
		if (germlineTransmissionStatus != null) {
			germlineTransmissionStatus.setSingleAllele(dbEntity);
			alleleGermlineTransmissionStatusDAO.persist(germlineTransmissionStatus);
		}
		dbEntity.setAlleleGermlineTransmissionStatus(germlineTransmissionStatus);

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

		if (secondaryIds != null) {
			for (AlleleSecondaryIdSlotAnnotation sid : secondaryIds) {
				sid.setSingleAllele(dbEntity);
				alleleSecondaryIdDAO.persist(sid);
			}
		}
		dbEntity.setAlleleSecondaryIds(secondaryIds);

		return dbEntity;
	}

	private Reference validateReference(Reference uiEntity, List<String> previousCuries) {
		ObjectResponse<Reference> singleRefResponse = referenceValidator.validateReference(uiEntity);
		if (singleRefResponse.getEntity() == null) {
			addMessageResponse("references", singleRefResponse.errorMessagesString());
			return null;
		}

		if (singleRefResponse.getEntity().getObsolete() && (CollectionUtils.isEmpty(previousCuries) || !previousCuries.contains(singleRefResponse.getEntity().getCurie()))) {
			addMessageResponse("references", "curie - " + ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return singleRefResponse.getEntity();
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

	private void removeUnusedAlleleMutationTypes(Allele uiEntity, Allele dbEntity) {
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

	private void removeUnusedAlleleInheritanceModes(Allele uiEntity, Allele dbEntity) {
		List<Long> reusedIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleInheritanceModes()))
			reusedIds = uiEntity.getAlleleInheritanceModes().stream().map(AlleleInheritanceModeSlotAnnotation::getId).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(dbEntity.getAlleleInheritanceModes())) {
			for (AlleleInheritanceModeSlotAnnotation previousInheritanceMode : dbEntity.getAlleleInheritanceModes()) {
				if (!reusedIds.contains(previousInheritanceMode.getId())) {
					previousInheritanceMode.setSingleAllele(null);
					alleleInheritanceModeDAO.remove(previousInheritanceMode.getId());
				}
			}
		}
	}
	
	private void removeUnusedAlleleGermlineTransmissionStatus(Allele uiEntity, Allele dbEntity) {
		Long reusedId = uiEntity.getAlleleGermlineTransmissionStatus() == null ? null : uiEntity.getAlleleGermlineTransmissionStatus().getId();
		AlleleGermlineTransmissionStatusSlotAnnotation previousStatus = dbEntity.getAlleleGermlineTransmissionStatus();
		
		if (previousStatus != null && (reusedId == null || !previousStatus.getId().equals(reusedId))) {
			previousStatus.setSingleAllele(null);
			alleleGermlineTransmissionStatusDAO.remove(previousStatus.getId());
		}
	}

	private void removeUnusedAlleleSymbol(Allele uiEntity, Allele dbEntity) {
		Long reusedId = uiEntity.getAlleleSymbol() == null ? null : uiEntity.getAlleleSymbol().getId();
		AlleleSymbolSlotAnnotation previousSymbol = dbEntity.getAlleleSymbol();

		if (previousSymbol != null && (reusedId == null || !previousSymbol.getId().equals(reusedId))) {
			previousSymbol.setSingleAllele(null);
			alleleSymbolDAO.remove(previousSymbol.getId());
		}
	}

	private void removeUnusedAlleleFullName(Allele uiEntity, Allele dbEntity) {
		Long reusedId = uiEntity.getAlleleFullName() == null ? null : uiEntity.getAlleleFullName().getId();
		AlleleFullNameSlotAnnotation previousFullName = dbEntity.getAlleleFullName();

		if (previousFullName != null && (reusedId == null || !previousFullName.getId().equals(reusedId))) {
			previousFullName.setSingleAllele(null);
			alleleFullNameDAO.remove(previousFullName.getId());
		}
	}

	private void removeUnusedAlleleSynonyms(Allele uiEntity, Allele dbEntity) {
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

	private void removeUnusedAlleleSecondaryIds(Allele uiEntity, Allele dbEntity) {
		List<Long> reusedIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleSecondaryIds()))
			reusedIds = uiEntity.getAlleleSecondaryIds().stream().map(AlleleSecondaryIdSlotAnnotation::getId).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(dbEntity.getAlleleSecondaryIds())) {
			for (AlleleSecondaryIdSlotAnnotation previousSecondaryId : dbEntity.getAlleleSecondaryIds()) {
				if (!reusedIds.contains(previousSecondaryId.getId())) {
					previousSecondaryId.setSingleAllele(null);
					alleleSecondaryIdDAO.remove(previousSecondaryId.getId());
				}
			}
		}
	}

	private List<AlleleMutationTypeSlotAnnotation> validateAlleleMutationTypes(Allele uiEntity, Allele dbEntity) {
		String field = "alleleMutationTypes";

		List<AlleleMutationTypeSlotAnnotation> validatedMutationTypes = new ArrayList<AlleleMutationTypeSlotAnnotation>();
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleMutationTypes())) {
			for (AlleleMutationTypeSlotAnnotation mt : uiEntity.getAlleleMutationTypes()) {
				ObjectResponse<AlleleMutationTypeSlotAnnotation> mtResponse = alleleMutationTypeValidator.validateAlleleMutationTypeSlotAnnotation(mt);
				if (mtResponse.getEntity() == null) {
					addMessageResponse(field, mtResponse.errorMessagesString());
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

	private List<AlleleInheritanceModeSlotAnnotation> validateAlleleInheritanceModes(Allele uiEntity, Allele dbEntity) {
		String field = "alleleInheritanceModes";

		List<AlleleInheritanceModeSlotAnnotation> validatedInheritanceModes = new ArrayList<AlleleInheritanceModeSlotAnnotation>();
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleInheritanceModes())) {
			for (AlleleInheritanceModeSlotAnnotation im : uiEntity.getAlleleInheritanceModes()) {
				ObjectResponse<AlleleInheritanceModeSlotAnnotation> imResponse = alleleInheritanceModeValidator.validateAlleleInheritanceModeSlotAnnotation(im);
				if (imResponse.getEntity() == null) {
					addMessageResponse(field, imResponse.errorMessagesString());
					return null;
				}
				im = imResponse.getEntity();
				validatedInheritanceModes.add(im);
			}
		}

		if (CollectionUtils.isEmpty(validatedInheritanceModes))
			return null;

		return validatedInheritanceModes;
	}
	
	private AlleleGermlineTransmissionStatusSlotAnnotation validateAlleleGermlineTransmissionStatus(Allele uiEntity, Allele dbEntity) {
		String field = "alleleGermlineTransmissionStatus";
		
		ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> agtsResponse = alleleGermlineTransmissionStatusValidator.validateAlleleGermlineTransmissionStatusSlotAnnotation(uiEntity.getAlleleGermlineTransmissionStatus());
		if (agtsResponse.getEntity() == null) {
			addMessageResponse(field, agtsResponse.errorMessagesString());
			return null;
		}
		
		return agtsResponse.getEntity();
	}

	private AlleleSymbolSlotAnnotation validateAlleleSymbol(Allele uiEntity, Allele dbEntity) {
		String field = "alleleSymbol";

		if (uiEntity.getAlleleSymbol() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<AlleleSymbolSlotAnnotation> symbolResponse = alleleSymbolValidator.validateAlleleSymbolSlotAnnotation(uiEntity.getAlleleSymbol());
		if (symbolResponse.getEntity() == null) {
			addMessageResponse(field, symbolResponse.errorMessagesString());
			return null;
		}

		return symbolResponse.getEntity();
	}

	private AlleleFullNameSlotAnnotation validateAlleleFullName(Allele uiEntity, Allele dbEntity) {
		if (uiEntity.getAlleleFullName() == null)
			return null;

		String field = "alleleFullName";

		ObjectResponse<AlleleFullNameSlotAnnotation> nameResponse = alleleFullNameValidator.validateAlleleFullNameSlotAnnotation(uiEntity.getAlleleFullName());
		if (nameResponse.getEntity() == null) {
			addMessageResponse(field, nameResponse.errorMessagesString());
			return null;
		}

		return nameResponse.getEntity();
	}

	private List<AlleleSynonymSlotAnnotation> validateAlleleSynonyms(Allele uiEntity, Allele dbEntity) {
		String field = "alleleSynonyms";

		List<AlleleSynonymSlotAnnotation> validatedSynonyms = new ArrayList<AlleleSynonymSlotAnnotation>();
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleSynonyms())) {
			for (AlleleSynonymSlotAnnotation syn : uiEntity.getAlleleSynonyms()) {
				ObjectResponse<AlleleSynonymSlotAnnotation> synResponse = alleleSynonymValidator.validateAlleleSynonymSlotAnnotation(syn);
				if (synResponse.getEntity() == null) {
					addMessageResponse(field, synResponse.errorMessagesString());
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

	private List<AlleleSecondaryIdSlotAnnotation> validateAlleleSecondaryIds(Allele uiEntity, Allele dbEntity) {
		String field = "alleleSecondaryIds";

		List<AlleleSecondaryIdSlotAnnotation> validatedSecondaryIds = new ArrayList<AlleleSecondaryIdSlotAnnotation>();
		if (CollectionUtils.isNotEmpty(uiEntity.getAlleleSecondaryIds())) {
			for (AlleleSecondaryIdSlotAnnotation sid : uiEntity.getAlleleSecondaryIds()) {
				ObjectResponse<AlleleSecondaryIdSlotAnnotation> sidResponse = alleleSecondaryIdValidator.validateAlleleSecondaryIdSlotAnnotation(sid);
				if (sidResponse.getEntity() == null) {
					addMessageResponse(field, sidResponse.errorMessagesString());
					return null;
				}
				sid = sidResponse.getEntity();
				validatedSecondaryIds.add(sid);
			}
		}

		if (CollectionUtils.isEmpty(validatedSecondaryIds))
			return null;

		return validatedSecondaryIds;
	}

	private void removeUnusedSlotAnnotations(Allele uiEntity, Allele dbEntity) {
		if (CollectionUtils.isNotEmpty(dbEntity.getAlleleMutationTypes()))
			removeUnusedAlleleMutationTypes(uiEntity, dbEntity);

		if (dbEntity.getAlleleSymbol() != null)
			removeUnusedAlleleSymbol(uiEntity, dbEntity);

		if (dbEntity.getAlleleFullName() != null)
			removeUnusedAlleleFullName(uiEntity, dbEntity);

		if (CollectionUtils.isNotEmpty(dbEntity.getAlleleSynonyms()))
			removeUnusedAlleleSynonyms(uiEntity, dbEntity);

		if (CollectionUtils.isNotEmpty(dbEntity.getAlleleSecondaryIds()))
			removeUnusedAlleleSecondaryIds(uiEntity, dbEntity);
		
		if (CollectionUtils.isNotEmpty(dbEntity.getAlleleInheritanceModes()))
			removeUnusedAlleleInheritanceModes(uiEntity, dbEntity);
		
		if (dbEntity.getAlleleGermlineTransmissionStatus() != null)
			removeUnusedAlleleGermlineTransmissionStatus(uiEntity, dbEntity);
	}
}
