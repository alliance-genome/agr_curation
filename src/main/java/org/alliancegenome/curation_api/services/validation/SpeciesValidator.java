package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.SpeciesDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class SpeciesValidator extends AuditedObjectValidator<Species> {

	@Inject SpeciesDAO speciesDAO;

	private String errorMessage;

	public Species validateSpeciesUpdate(Species uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Species: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Species ID provided");
			throw new ApiErrorException(response);
		}
		Species dbEntity = speciesDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Species with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}

		dbEntity = (Species) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateSpecies(uiEntity, dbEntity);
	}

	public Species validateSpeciesCreate(Species uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create Species";

		Species dbEntity = new Species();

		dbEntity = (Species) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateSpecies(uiEntity, dbEntity);
	}

	private Species validateSpecies(Species uiEntity, Species dbEntity) {
		NCBITaxonTerm taxon = validateRequiredTaxon(uiEntity.getTaxon(), dbEntity.getTaxon());
		dbEntity.setTaxon(taxon);

		if (StringUtils.isBlank(uiEntity.getFullName())) {
			addMessageResponse("fullName", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			dbEntity.setFullName(uiEntity.getFullName());
		}

		if (StringUtils.isBlank(uiEntity.getDisplayName())) {
			addMessageResponse("displayName", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			dbEntity.setDisplayName(uiEntity.getDisplayName());
		}

		if (StringUtils.isBlank(uiEntity.getAbbreviation())) {
			addMessageResponse("abbreviation", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			dbEntity.setAbbreviation(uiEntity.getAbbreviation());
		}

		Organization dataProvider = validateDataProvider(uiEntity.getDataProvider(), dbEntity.getDataProvider(), false);
		dbEntity.setDataProvider(dataProvider);

		CrossReference dataProviderCrossReference = validateDataProviderCrossReference(uiEntity.getDataProviderCrossReference(), dbEntity.getDataProviderCrossReference());
		dbEntity.setDataProviderCrossReference(dataProviderCrossReference);

		if (uiEntity.getPhylogeneticOrder() == null) {
			addMessageResponse("phylogeneticOrder", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			dbEntity.setPhylogeneticOrder(uiEntity.getPhylogeneticOrder());
		}

		if (StringUtils.isBlank(uiEntity.getAssembly_curie())) {
			addMessageResponse("assembly_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			dbEntity.setAssembly_curie(uiEntity.getAssembly_curie());
		}

		dbEntity.setCommonNames(uiEntity.getCommonNames());

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}
}
