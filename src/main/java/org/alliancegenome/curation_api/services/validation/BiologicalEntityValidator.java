package org.alliancegenome.curation_api.services.validation;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.enums.SupportedSpecies;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.lang3.StringUtils;

public class BiologicalEntityValidator extends CurieAuditedObjectValidator {

	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	@Inject
	DataProviderService dataProviderService;
	@Inject
	DataProviderValidator dataProviderValidator;
	@Inject
	DataProviderDAO dataProviderDAO;

	public NCBITaxonTerm validateTaxon(BiologicalEntity uiEntity, BiologicalEntity dbEntity) {
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

	public DataProvider validateDataProvider(BiologicalEntity uiEntity, BiologicalEntity dbEntity) {
		String field = "dataProvider";
		if (uiEntity.getDataProvider() == null) {
			uiEntity.setDataProvider(dataProviderService.createAffiliatedModDataProvider());
			if (uiEntity.getDataProvider() == null) {
				addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
				return null;
			}
		}
		
		DataProvider uiDataProvider = uiEntity.getDataProvider();
		DataProvider dbDataProvider = dbEntity.getDataProvider();
		
		ObjectResponse<DataProvider> dpResponse = dataProviderValidator.validateDataProvider(uiDataProvider, dbDataProvider, false);
		if (dpResponse.hasErrors()) {
			addMessageResponse(field, dpResponse.errorMessagesString());
			return null;
		}
		
		DataProvider validatedDataProvider = dpResponse.getEntity();
		if (validatedDataProvider.getObsolete() && (dbDataProvider == null || !validatedDataProvider.getId().equals(dbDataProvider.getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		
		if (validatedDataProvider.getId() == null)
			validatedDataProvider = dataProviderDAO.persist(validatedDataProvider);
		
		return validatedDataProvider;
	}
}
