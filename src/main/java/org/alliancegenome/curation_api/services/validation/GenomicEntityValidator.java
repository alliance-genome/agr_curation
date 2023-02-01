package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.enums.SupportedSpecies;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;

public class GenomicEntityValidator extends CurieAuditedObjectValidator {

	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	@Inject
	SynonymDAO synonymDAO;
	@Inject
	CrossReferenceValidator crossReferenceValidator;
	@Inject
	CrossReferenceService crossReferenceService;

	public NCBITaxonTerm validateTaxon(GenomicEntity uiEntity, GenomicEntity dbEntity) {
		String field = "taxon";
		if (uiEntity.getTaxon() == null || StringUtils.isBlank(uiEntity.getTaxon().getCurie())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		NCBITaxonTerm taxon = ncbiTaxonTermService.get(uiEntity.getTaxon().getCurie()).getEntity();
		if (taxon == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		
		if (!SupportedSpecies.isSupported(taxon.getGenusSpecies())) {
			addMessageResponse(field, ValidationConstants.UNSUPPORTED_MESSAGE);
			return null;
		}
		
		if (taxon.getObsolete() && (dbEntity.getTaxon() == null || !taxon.getCurie().equals(dbEntity.getTaxon().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		
		return taxon;
	}

	public List<CrossReference> validateCrossReferences(GenomicEntity uiEntity, GenomicEntity dbEntity) {
		String field = "crossReferences";

		List<CrossReference> validatedXrefs = new ArrayList<CrossReference>();
		if (CollectionUtils.isNotEmpty(uiEntity.getCrossReferences())) {
			for (CrossReference newXref : uiEntity.getCrossReferences()) {
				ObjectResponse<CrossReference> xrefResponse = crossReferenceValidator.validateCrossReference(newXref, false);
				if (xrefResponse.getEntity() == null) {
					addMessageResponse(field, xrefResponse.errorMessagesString());
					return null;
				}
				validatedXrefs.add(xrefResponse.getEntity());
			}
		}
		
		validatedXrefs = crossReferenceService.handleUpdate(validatedXrefs, dbEntity.getCrossReferences());

		if (CollectionUtils.isEmpty(validatedXrefs))
			return null;

		return validatedXrefs;
	}

}
