package org.alliancegenome.curation_api.services.validation.dto.fms;


import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.SQTR;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.sqtrSlotAnnotations.SQTRSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.fms.SQTRFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;



@RequestScoped
public class SQTRFmsDTOValidator {
	@Inject
	DataProviderService dataProviderService;

	public ObjectResponse<SQTR> validateSQTRFmsDTO (SQTRFmsDTO dto, BackendBulkDataProvider beDataProvider) {
		ObjectResponse<SQTR> sqtrResponse = new ObjectResponse<>();
		
		SQTR sqtr = new SQTR();

		//TODO: does this need to look up the name in order to update or ensure uniqueness?
		if (StringUtils.isBlank(dto.getPrimaryId())) {
			sqtrResponse.addErrorMessage("primaryId", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			//TODO:this probably isn't correct
			sqtr.setCurie(dto.getPrimaryId());
		}

		//TODO:does this need to look up the name in order to update or ensure uniqueness?
		if (StringUtils.isBlank(dto.getName())) {
			sqtrResponse.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			sqtr.setName(dto.getName());
		}

		if (StringUtils.isNotBlank(dto.getTaxonId())) {
			NCBITaxonTerm taxonTerm = new NCBITaxonTerm();
			taxonTerm.setCurie(dto.getTaxonId());;
			sqtr.setTaxon(taxonTerm);
		}

		if (CollectionUtils.isNotEmpty(dto.getSynonyms())) {
			List<Synonym> synonyms = new ArrayList<>();
			for (String synonymName : dto.getSynonyms()) {
				if (StringUtils.isNotBlank(synonymName)) {
					Synonym synonym = new Synonym();
					synonym.setName(synonymName);
					synonyms.add(synonym);
				}
			}
			sqtr.setSynonyms(synonyms);	
		} else {
			sqtr.setSynonyms(null);	
		}

		if (CollectionUtils.isNotEmpty(dto.getSecondaryIds())) {
			List<SecondaryIdSlotAnnotation> secondaryIds = new ArrayList<>();
			for (String secondaryId : dto.getSecondaryIds()) {
				if (StringUtils.isNotBlank(secondaryId)) {
					SecondaryIdSlotAnnotation secondaryIdSlotAnnotation = new SQTRSecondaryIdSlotAnnotation();
					secondaryIdSlotAnnotation.setSecondaryId(secondaryId);
					secondaryIds.add(secondaryIdSlotAnnotation);
				}
			}
			sqtr.setSecondaryIdentifiers(secondaryIds);	
		} else {
			sqtr.setSecondaryIdentifiers(null);	
		}

		sqtr.setDataProvider(dataProviderService.createOrganizationDataProvider(beDataProvider.sourceOrganization));

		return sqtrResponse;
	}
}
