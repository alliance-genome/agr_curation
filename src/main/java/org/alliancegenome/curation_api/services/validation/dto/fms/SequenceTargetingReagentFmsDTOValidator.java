package org.alliancegenome.curation_api.services.validation.dto.fms;


import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.SequenceTargetingReagentDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SequenceTargetingReagentFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import io.quarkus.logging.Log;

@RequestScoped
public class SequenceTargetingReagentFmsDTOValidator {
	@Inject
	DataProviderService dataProviderService;

	@Inject
	SequenceTargetingReagentDAO sqtrDAO;

	@Inject NcbiTaxonTermService ncbiTaxonTermService;

	public SequenceTargetingReagent validateSQTRFmsDTO (SequenceTargetingReagentFmsDTO dto, BackendBulkDataProvider beDataProvider) throws ObjectValidationException {
		ObjectResponse<SequenceTargetingReagent> sqtrResponse = new ObjectResponse<>();
		
		SequenceTargetingReagent sqtr;
		
		if (StringUtils.isBlank(dto.getPrimaryId())) {
			sqtrResponse.addErrorMessage("primaryId", ValidationConstants.REQUIRED_MESSAGE);
			sqtr = new SequenceTargetingReagent();
		} else {
			SearchResponse<SequenceTargetingReagent> searchResponse = sqtrDAO.findByField("curie", dto.getPrimaryId());
			if (searchResponse == null || searchResponse.getSingleResult() == null) {
				sqtr = new SequenceTargetingReagent();
				sqtr.setCurie(dto.getPrimaryId());
			} else {
				sqtr = searchResponse.getSingleResult();
			}
		}


		if (StringUtils.isBlank(dto.getName())) {
			sqtrResponse.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			sqtr.setName(dto.getName());
		}

		//TODO: should I just use the validateBiologicalEntityDTO here?
		if (StringUtils.isBlank(dto.getTaxonId())) {
			sqtrResponse.addErrorMessage("taxonId", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.getByCurie(dto.getTaxonId());
			if (taxonResponse.getEntity() == null) {
				sqtrResponse.addErrorMessage("taxonId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTaxonId() + ")");
			}
			if (beDataProvider != null && (beDataProvider.name().equals("RGD") || beDataProvider.name().equals("HUMAN")) && !taxonResponse.getEntity().getCurie().equals(beDataProvider.canonicalTaxonCurie)) {
				sqtrResponse.addErrorMessage("taxonId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTaxonId() + ") for " + beDataProvider.name() + " load");
			}
			sqtr.setTaxon(taxonResponse.getEntity());
		}
		
		if (CollectionUtils.isNotEmpty(dto.getSynonyms())) {
			sqtr.setSynonyms(dto.getSynonyms());	
		} else {
			sqtr.setSynonyms(null);	
		}

		if (CollectionUtils.isNotEmpty(dto.getSecondaryIds())) {
			sqtr.setSecondaryIdentifiers(dto.getSecondaryIds());	
		} else {
			sqtr.setSecondaryIdentifiers(null);	
		}
		
		sqtr.setDataProvider(dataProviderService.createOrganizationDataProvider(beDataProvider.sourceOrganization));
		
		if (sqtrResponse.hasErrors()){
			throw new ObjectValidationException(dto, sqtrResponse.errorMessagesString());
		}
			
		//TODO: remove when appropriate
		Log.info("inside validation");
		Log.info("curie");
		Log.info(sqtr.getCurie());
		Log.info("name");
		Log.info(sqtr.getName());
		Log.info("synonyms");
		Log.info(sqtr.getSynonyms());
		Log.info("taxon id");
		Log.info(sqtr.getTaxon());
		Log.info("secondary ids");
		Log.info(sqtr.getSecondaryIdentifiers());
		return sqtr;
	}
}
