package org.alliancegenome.curation_api.services.helpers.validators;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.lang3.StringUtils;

public class GenomicEntityValidator extends CurieAuditedObjectValidator {

	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	
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
	
}
