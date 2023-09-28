package org.alliancegenome.curation_api.services.validation;

import java.util.List;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Reagent;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class ReagentValidator extends AuditedObjectValidator<Reagent> {

	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	@Inject
	DataProviderService dataProviderService;
	@Inject
	DataProviderValidator dataProviderValidator;

	public Reagent validateCommonReagentFields(Reagent uiEntity, Reagent dbEntity) {
		
		Boolean newEntity = false;
		if (dbEntity.getId() == null)
			newEntity = true;
		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity, newEntity);
		
		String modEntityId = StringUtils.isNotBlank(uiEntity.getModEntityId()) ? uiEntity.getModEntityId() : null;
		dbEntity.setModEntityId(modEntityId);
		
		String modInternalId = StringUtils.isNotBlank(uiEntity.getModInternalId()) ? uiEntity.getModInternalId() : null;
		dbEntity.setModInternalId(modInternalId);
		
		List<String> secondaryIds = CollectionUtils.isNotEmpty(uiEntity.getSecondaryIdentifiers()) ? uiEntity.getSecondaryIdentifiers() : null;
		dbEntity.setSecondaryIdentifiers(secondaryIds);
		
		DataProvider dataProvider = validateDataProvider(uiEntity, dbEntity);
		dbEntity.setDataProvider(dataProvider);
		
		return dbEntity;
	}
	
	public DataProvider validateDataProvider(Reagent uiEntity, Reagent dbEntity) {
		String field = "dataProvider";
		
		if (uiEntity.getDataProvider() == null) {
			if (dbEntity.getId() == null)
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
		
		return validatedDataProvider;
	}
}
