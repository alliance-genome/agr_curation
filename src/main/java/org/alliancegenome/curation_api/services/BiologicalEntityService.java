package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.BiologicalEntityDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class BiologicalEntityService<E extends BiologicalEntity, D extends BiologicalEntityDTO> extends BaseEntityCrudService<BiologicalEntity, BiologicalEntityDAO> {

	@Inject BiologicalEntityDAO biologicalEntityDAO;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(biologicalEntityDAO);
	}
	
	public ObjectResponse<E> validateBiologicalEntityDTO (E entity, D dto) {
		
		ObjectResponse<E> response = new ObjectResponse<E>();
		
		if (StringUtils.isBlank(dto.getTaxon())) {
			response.addErrorMessage("taxon", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.get(dto.getTaxon());
			if (taxonResponse.getEntity() == null) {
				response.addErrorMessage("taxon", ValidationConstants.INVALID_MESSAGE);
			}
			entity.setTaxon(taxonResponse.getEntity());
		}
			
		response.setEntity(entity);
		
		return response;
	}
}
