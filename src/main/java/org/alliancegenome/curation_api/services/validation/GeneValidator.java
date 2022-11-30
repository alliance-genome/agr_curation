package org.alliancegenome.curation_api.services.validation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections4.CollectionUtils;

@RequestScoped
public class GeneValidator extends GenomicEntityValidator {
	
	@Inject
	GeneDAO geneDAO;
	@Inject
	SoTermDAO soTermDAO;
	
	public Gene validateAnnotation(Gene uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		
		String curie = validateCurie(uiEntity);
		if (curie == null) {
			throw new ApiErrorException(response);
		}
		
		Gene dbEntity = geneDAO.find(curie);
		if (dbEntity == null) {
			addMessageResponse("Could not find allele with curie: [" + curie + "]");
			throw new ApiErrorException(response);
		}
		
		String errorTitle = "Could not update allele [" + curie + "]";
		
		dbEntity = (Gene) validateAuditedObjectFields(uiEntity, dbEntity, false);

		NCBITaxonTerm taxon = validateTaxon(uiEntity);
		dbEntity.setTaxon(taxon);
		
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
		
		SOTerm geneType = validateGeneType(uiEntity, dbEntity);
		dbEntity.setGeneType(geneType);
		
		if (response.hasErrors()) {
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}
		
		return dbEntity;
	}
	
	private SOTerm validateGeneType(Gene uiEntity, Gene dbEntity) {
		SOTerm soTerm = soTermDAO.find(uiEntity.getGeneType().getCurie());
		if (soTerm == null) {
			addMessageResponse("geneType", ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		else if (soTerm.getObsolete() && (dbEntity.getGeneType() == null || !soTerm.getCurie().equals(dbEntity.getGeneType().getCurie()))) {
			addMessageResponse("geneType", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return soTerm;
	}

}
