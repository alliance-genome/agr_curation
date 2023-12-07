package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.base.SubmittedObjectValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class BiologicalEntityValidator<E extends BiologicalEntity> extends SubmittedObjectValidator<E> {

	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	
	public E validateBiologicalEntityFields(E uiEntity, E dbEntity) {
		
		dbEntity = validateSubmittedObjectFields(uiEntity, dbEntity);
		
		NCBITaxonTerm taxon = validateTaxon(uiEntity, dbEntity);
		dbEntity.setTaxon(taxon);
		
		return dbEntity;
	}

	public NCBITaxonTerm validateTaxon(E uiEntity, E dbEntity) {
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
		
		if (taxon.getObsolete() && (dbEntity.getTaxon() == null || !taxon.getCurie().equals(dbEntity.getTaxon().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		
		return taxon;
	}
}
