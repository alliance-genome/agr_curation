package org.alliancegenome.curation_api.services.validation;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AffectedGenomicModelValidator extends GenomicEntityValidator {

	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;
	
	private String errorMessage;

	public AffectedGenomicModel validateAffectedGenomicModelUpdate(AffectedGenomicModel uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update AGM: [" + uiEntity.getCurie() + "]";

		String curie = validateCurie(uiEntity);
		if (curie == null) {
			throw new ApiErrorException(response);
		}

		AffectedGenomicModel dbEntity = affectedGenomicModelDAO.find(curie);
		if (dbEntity == null) {
			addMessageResponse("curie", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}
		
		dbEntity = (AffectedGenomicModel) validateAuditedObjectFields(uiEntity, dbEntity, false);
		
		return validateAffectedGenomicModel(uiEntity, dbEntity);
	}
	
	public AffectedGenomicModel validateAffectedGenomicModelCreate(AffectedGenomicModel uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create AGM [" + uiEntity.getCurie() + "]";

		AffectedGenomicModel dbEntity = new AffectedGenomicModel();
		String curie = validateCurie(uiEntity);
		dbEntity.setCurie(curie);
		
		dbEntity = (AffectedGenomicModel) validateAuditedObjectFields(uiEntity, dbEntity, true);
		
		return validateAffectedGenomicModel(uiEntity, dbEntity);
	}
	
	private AffectedGenomicModel validateAffectedGenomicModel(AffectedGenomicModel uiEntity, AffectedGenomicModel dbEntity) {

		String name = handleStringField(uiEntity.getName());
		dbEntity.setName(name);

		NCBITaxonTerm taxon = validateTaxon(uiEntity);
		dbEntity.setTaxon(taxon);

		if (CollectionUtils.isNotEmpty(uiEntity.getSecondaryIdentifiers())) {
			dbEntity.setSecondaryIdentifiers(uiEntity.getSecondaryIdentifiers());
		} else {
			dbEntity.setSecondaryIdentifiers(null);
		}

		List<CrossReference> crossReferences = validateCrossReferences(uiEntity, dbEntity);
		dbEntity.setCrossReferences(crossReferences);

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}

}
