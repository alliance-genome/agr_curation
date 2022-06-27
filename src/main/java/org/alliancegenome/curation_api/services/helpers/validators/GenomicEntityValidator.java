package org.alliancegenome.curation_api.services.helpers.validators;

import javax.inject.Inject;

import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.lang3.StringUtils;
import org.alliancegenome.curation_api.constants.ValidationConstants;

public class GenomicEntityValidator extends CurieAuditedObjectValidator {

	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	
	public NCBITaxonTerm validateTaxon(GenomicEntity uiEntity) {
		String taxonCurie = uiEntity.getTaxon().getCurie();
		if (StringUtils.isBlank(taxonCurie)) {
			addMessageResponse("taxon", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		ObjectResponse<NCBITaxonTerm> taxon = ncbiTaxonTermService.get(taxonCurie);
		if (taxon.getEntity() == null) {
			addMessageResponse("taxon", ValidationConstants.INVALID_MESSAGE);
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
