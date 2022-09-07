package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleValidator extends GenomicEntityValidator {
	
	@Inject
	AlleleDAO alleleDAO;
	
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
	
		dbEntity.setDescription(handleStringField(uiEntity.getDescription()));		
		
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

}
