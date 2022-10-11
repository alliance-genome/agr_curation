package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.dto.GenomicEntityDTO;
import org.alliancegenome.curation_api.model.ingest.dto.SynonymDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class GenomicEntityDTOValidator <E extends GenomicEntity, D extends GenomicEntityDTO> {
	
	@Inject SynonymDTOValidator synonymDtoValidator;
	@Inject BiologicalEntityDTOValidator<E, D> biologicalEntityDtoValidator;
	
	public ObjectResponse<E> validateGenomicEntityDTO (E entity, D dto) {
		
		ObjectResponse<E> geResponse = new ObjectResponse<E>();
		
		ObjectResponse<E> beResponse = biologicalEntityDtoValidator.validateBiologicalEntityDTO(entity, dto);
		geResponse.addErrorMessages(beResponse.getErrorMessages());
		entity = beResponse.getEntity();
		
		if (StringUtils.isNotBlank(dto.getName()))
			entity.setName(dto.getName());
	
		if (CollectionUtils.isNotEmpty(dto.getSynonyms())) {
			List<Synonym> synonyms = new ArrayList<>();
			for (SynonymDTO synonymDto : dto.getSynonyms()) {
				ObjectResponse<Synonym> synResponse = synonymDtoValidator.validateSynonymDTO(synonymDto);
				if (synResponse.hasErrors()) {
					geResponse.addErrorMessage("synonyms", synResponse.errorMessagesString());
				} else {
					synonyms.add(synResponse.getEntity());
				}
			}
		}
		
		geResponse.setEntity(entity);
		
		return geResponse;
	}
}
