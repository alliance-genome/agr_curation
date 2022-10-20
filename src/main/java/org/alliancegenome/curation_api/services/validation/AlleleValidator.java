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
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleValidator extends GenomicEntityValidator {
	
	@Inject AlleleDAO alleleDAO;
	@Inject ReferenceValidator referenceValidator;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	
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
		
		String symbol = validateSymbol(uiEntity);
		dbEntity.setSymbol(symbol);
		
		String name = StringUtils.isNotBlank(uiEntity.getName()) ? uiEntity.getName() : null;
		dbEntity.setName(name);

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

		VocabularyTerm sequencingStatus = validateSequencingStatus(uiEntity, dbEntity);
		dbEntity.setSequencingStatus(sequencingStatus);
		
		if(uiEntity.getIsExtinct() != null) {
			dbEntity.setIsExtinct(uiEntity.getIsExtinct());
		}else{
			dbEntity.setIsExtinct(null);
		}
		
		List<Synonym> synonyms = validateSynonyms(uiEntity, dbEntity);
		dbEntity.setSynonyms(synonyms);

		List<CrossReference> crossReferences = validateCrossReferences(uiEntity, dbEntity);
		dbEntity.setCrossReferences(crossReferences);
		
		if (CollectionUtils.isNotEmpty(uiEntity.getSecondaryIdentifiers())) {
			dbEntity.setSecondaryIdentifiers(uiEntity.getSecondaryIdentifiers());
		} else {
			dbEntity.setSecondaryIdentifiers(null);
		}
	
		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}
		
		return dbEntity;
	}
	
	private String validateSymbol(Allele uiEntity) {
		String symbol = uiEntity.getSymbol();
		if (StringUtils.isBlank(symbol)) {
			addMessageResponse("symbol", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		return symbol;
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

	public VocabularyTerm validateInheritanceMode(Allele uiEntity, Allele dbEntity) {
		String field = "inheritanceMode";

		if (uiEntity.getInheritanceMode() == null)
			return null;
		
		VocabularyTerm inheritanceMode = vocabularyTermDAO.getTermInVocabulary(uiEntity.getInheritanceMode().getName(), VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY);
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

	public VocabularyTerm validateInCollection(Allele uiEntity, Allele dbEntity) {
		String field = "inCollection";

		if (uiEntity.getInCollection() == null)
			return null;

		VocabularyTerm inCollection = vocabularyTermDAO.getTermInVocabulary(uiEntity.getInCollection().getName(), VocabularyConstants.ALLELE_COLLECTION_VOCABULARY);
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

	public VocabularyTerm validateSequencingStatus(Allele uiEntity, Allele dbEntity) {
		String field = "sequencingStatus";

		if (uiEntity.getSequencingStatus() == null)
			return null;

		VocabularyTerm sequencingStatus = vocabularyTermDAO.getTermInVocabulary(uiEntity.getSequencingStatus().getName(), VocabularyConstants.SEQUENCING_STATUS_VOCABULARY);
		if (sequencingStatus == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (sequencingStatus.getObsolete() && (dbEntity.getSequencingStatus() == null || !sequencingStatus.getName().equals(dbEntity.getSequencingStatus().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return sequencingStatus;
	}

}
