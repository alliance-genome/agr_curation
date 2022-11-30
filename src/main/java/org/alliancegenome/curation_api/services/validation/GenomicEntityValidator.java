package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;

public class GenomicEntityValidator extends CurieAuditedObjectValidator {

	@Inject NcbiTaxonTermService ncbiTaxonTermService;
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
