package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class AffectedGenomicModelValidator extends GenomicEntityValidator {
	
	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;
	
	public AffectedGenomicModel validateAnnotation(AffectedGenomicModel uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		
		String curie = validateCurie(uiEntity);
		if (curie == null) {
			throw new ApiErrorException(response);
		}
		
		AffectedGenomicModel dbEntity = affectedGenomicModelDAO.find(curie);
		if (dbEntity == null) {
			addMessageResponse("Could not find AGM with curie: [" + curie + "]");
			throw new ApiErrorException(response);
		}
		
		String errorTitle = "Could not update AGM [" + curie + "]";
		
		dbEntity = (AffectedGenomicModel) validateAuditedObjectFields(uiEntity, dbEntity, false);

		String name = validateName(uiEntity);
		dbEntity.setName(name);
		
		NCBITaxonTerm taxon = validateTaxon(uiEntity);
		dbEntity.setTaxon(taxon);
		
		dbEntity.setSubtype(uiEntity.getSubtype());
		
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
		
		if (response.hasErrors()) {
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}
		
		return dbEntity;
	}

}
