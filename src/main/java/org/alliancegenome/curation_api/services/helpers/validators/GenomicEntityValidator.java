package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;

public class GenomicEntityValidator extends CurieAuditedObjectValidator {

	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject SynonymValidator synonymValidator;
	@Inject SynonymDAO synonymDAO;
	@Inject CrossReferenceValidator crossReferenceValidator;
	@Inject CrossReferenceDAO crossReferenceDAO;
	
	public NCBITaxonTerm validateTaxon(GenomicEntity uiEntity) {
		String field = "taxon";
		if (uiEntity.getTaxon() == null || StringUtils.isBlank(uiEntity.getTaxon().getCurie())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		
		ObjectResponse<NCBITaxonTerm> taxon = ncbiTaxonTermService.get(uiEntity.getTaxon().getCurie());
		if (taxon.getEntity() == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		return taxon.getEntity();
	}
	
	public String validateName(GenomicEntity uiEntity) {
		String name = uiEntity.getName();
		if (StringUtils.isBlank(name)) {
			addMessageResponse("name", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		return name;
	}
	
	public List<Synonym> validateSynonyms(GenomicEntity uiEntity, GenomicEntity dbEntity) {
		String field = "synonyms";
		
		List<Synonym> validatedSynonyms = new ArrayList<Synonym>();
		if (CollectionUtils.isNotEmpty(uiEntity.getSynonyms())) {	
			for (Synonym newSynonym : uiEntity.getSynonyms()) {
				ObjectResponse<Synonym> synonymResponse = synonymValidator.validateSynonym(newSynonym);
				if (synonymResponse.getEntity() == null) {
					Map<String, String> errors = synonymResponse.getErrorMessages();
					for (String synonymField : errors.keySet()) {
						addMessageResponse(field, synonymField + " - " + errors.get(synonymField));
					}
					return null;
				}
				validatedSynonyms.add(synonymResponse.getEntity());
			}
		}
		
		List<Long> previousIds = new ArrayList<Long>();
		if(CollectionUtils.isNotEmpty(dbEntity.getSynonyms()))
			previousIds = dbEntity.getSynonyms().stream().map(Synonym::getId).collect(Collectors.toList());
		List<Long> validatedIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(validatedSynonyms))
			validatedIds = validatedSynonyms.stream().map(Synonym::getId).collect(Collectors.toList());
		for (Synonym validatedSynonym : validatedSynonyms) {
			if (!previousIds.contains(validatedSynonym.getId()))
				synonymDAO.persist(validatedSynonym);
		}
		
		if (dbEntity.getSynonyms() != null) {
			List<Long> idsToRemove = ListUtils.subtract(previousIds, validatedIds);
			Predicate<Synonym> removeCondition = synonym -> idsToRemove.contains(synonym.getId());
			dbEntity.getSynonyms().removeIf(removeCondition);
		}
		
		if (CollectionUtils.isEmpty(validatedSynonyms))
			return null;
		
		return validatedSynonyms;
	}
	
	public List<CrossReference> validateCrossReferences(GenomicEntity uiEntity, GenomicEntity dbEntity) {
		String field = "crossReferences";
		
		List<CrossReference> validatedXrefs = new ArrayList<CrossReference>();
		if (CollectionUtils.isNotEmpty(uiEntity.getCrossReferences())) {	
			for (CrossReference newXref : uiEntity.getCrossReferences()) {
				ObjectResponse<CrossReference> xrefResponse = crossReferenceValidator.validateCrossReference(newXref);
				if (xrefResponse.getEntity() == null) {
					Map<String, String> errors = xrefResponse.getErrorMessages();
					for (String xrefField : errors.keySet()) {
						addMessageResponse(field, xrefField + " - " + errors.get(xrefField));
					}
					return null;
				}
				validatedXrefs.add(xrefResponse.getEntity());
			}
		}
		
		List<String> previousCuries = new ArrayList<String>();
		if(CollectionUtils.isNotEmpty(dbEntity.getCrossReferences()))
			previousCuries = dbEntity.getCrossReferences().stream().map(CrossReference::getCurie).collect(Collectors.toList());
		List<String> validatedCuries = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(validatedXrefs))
			validatedCuries = validatedXrefs.stream().map(CrossReference::getCurie).collect(Collectors.toList());
		for (CrossReference validatedXref : validatedXrefs) {
			if (!previousCuries.contains(validatedXref.getCurie()))
				crossReferenceDAO.persist(validatedXref);
		}
		
		if (dbEntity.getCrossReferences() != null) {
			List<String> curiesToRemove = ListUtils.subtract(previousCuries, validatedCuries);
			Predicate<CrossReference> removeCondition = xref -> curiesToRemove.contains(xref.getCurie());
			dbEntity.getCrossReferences().removeIf(removeCondition);
		}
		
		if (CollectionUtils.isEmpty(validatedXrefs))
			return null;
		
		return validatedXrefs;
	}
	
}
