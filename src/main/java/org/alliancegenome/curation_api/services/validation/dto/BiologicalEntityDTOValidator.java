package org.alliancegenome.curation_api.services.validation.dto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.BiologicalEntityDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class BiologicalEntityDTOValidator<E extends BiologicalEntity, D extends BiologicalEntityDTO> {
	
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject AuditedObjectDTOValidator<E, D> auditedObjectDtoValidator;
	
	public ObjectResponse<E> validateBiologicalEntityDTO (E entity, D dto) {
		
		ObjectResponse<E> beResponse = new ObjectResponse<E>();
		
		ObjectResponse<E> aoResponse = auditedObjectDtoValidator.validateAuditedObjectDTO(entity, dto);
		beResponse.addErrorMessages(aoResponse.getErrorMessages());
		entity = aoResponse.getEntity();
		
		if (StringUtils.isBlank(dto.getTaxon())) {
			beResponse.addErrorMessage("taxon", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.get(dto.getTaxon());
			if (taxonResponse.getEntity() == null) {
				beResponse.addErrorMessage("taxon", ValidationConstants.INVALID_MESSAGE);
			}
			entity.setTaxon(taxonResponse.getEntity());
		}
			
		beResponse.setEntity(entity);
		
		return beResponse;
	}

}
