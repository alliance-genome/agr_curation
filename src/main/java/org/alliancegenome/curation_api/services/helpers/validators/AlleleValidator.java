package org.alliancegenome.curation_api.services.helpers.validators;

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
import org.alliancegenome.curation_api.model.entities.Reference;
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
	
	public Allele validateAnnotation(Allele uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		
		String curie = validateCurie(uiEntity);
		if (curie == null) {
			throw new ApiErrorException(response);
		}
		
		Allele dbEntity = alleleDAO.find(curie);
		if (dbEntity == null) {
			addMessageResponse("Could not find allele with curie: [" + curie + "]");
			throw new ApiErrorException(response);
		}

		String errorTitle = "Could not update allele [" + curie + "]";
		
		dbEntity = (Allele) validateAuditedObjectFields(uiEntity, dbEntity, false);

		NCBITaxonTerm taxon = validateTaxon(uiEntity);
		dbEntity.setTaxon(taxon);
		
		String symbol = validateSymbol(uiEntity);
		dbEntity.setSymbol(symbol);
		
		String name = validateName(uiEntity);
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

		VocabularyTerm inheritenceMode = validateInheritenceMode(uiEntity, dbEntity);
		dbEntity.setInheritenceMode(inheritenceMode);

		VocabularyTerm inCollection = validateInCollection(uiEntity, dbEntity);
		dbEntity.setInCollection(inCollection);

		VocabularyTerm sequencingStatus = validateSequencingStatus(uiEntity, dbEntity);
		dbEntity.setSequencingStatus(sequencingStatus);
		
		if(uiEntity.getIsExtinct() != null) {
			dbEntity.setIsExtinct(uiEntity.getIsExtinct());
		}else{
			dbEntity.setIsExtinct(false);
		}
		
		if (CollectionUtils.isNotEmpty(uiEntity.getSynonyms())) {
			dbEntity.setSynonyms(uiEntity.getSynonyms());
		} else {
			dbEntity.setSynonyms(null);
		}

		if (CollectionUtils.isNotEmpty(uiEntity.getSecondaryIdentifiers())) {
			dbEntity.setSecondaryIdentifiers(uiEntity.getSecondaryIdentifiers());
		} else {
			dbEntity.setSecondaryIdentifiers(null);
		}

		if (CollectionUtils.isNotEmpty(uiEntity.getCrossReferences())) {
			dbEntity.setCrossReferences(uiEntity.getCrossReferences());
		} else {
			dbEntity.setCrossReferences(null);
		}
	
		if (response.hasErrors()) {
			response.setErrorMessage(errorTitle);
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

	public VocabularyTerm validateInheritenceMode(Allele uiEntity, Allele dbEntity) {
		String field = "inheritenceMode";

		if (uiEntity.getInheritenceMode() == null)
			return null;
		
		VocabularyTerm inheritenceMode = vocabularyTermDAO.getTermInVocabulary(uiEntity.getInheritenceMode().getName(), VocabularyConstants.ALLELE_INHERITENCE_MODE_VOCABULARY);
		if (inheritenceMode == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (inheritenceMode.getObsolete() && (dbEntity.getInheritenceMode() == null || !inheritenceMode.getName().equals(dbEntity.getInheritenceMode().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return inheritenceMode;
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
