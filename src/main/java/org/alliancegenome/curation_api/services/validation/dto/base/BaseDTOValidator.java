package org.alliancegenome.curation_api.services.validation.dto.base;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.BiologicalEntityDTO;
import org.alliancegenome.curation_api.model.ingest.dto.GenomicEntityDTO;
import org.alliancegenome.curation_api.model.ingest.dto.SynonymDTO;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class BaseDTOValidator {

	@Inject PersonService personService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject SynonymDAO synonymDAO;
	
	public <E extends AuditedObject, D extends AuditedObjectDTO> ObjectResponse<E> validateAuditedObjectDTO (E entity, D dto) {
		
		ObjectResponse<E> response = new ObjectResponse<E>();
		
		Person createdBy = null;
		if (StringUtils.isNotBlank(dto.getCreatedBy()))
			createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedBy());
		entity.setCreatedBy(createdBy);
		
		Person updatedBy = null;
		if (StringUtils.isNotBlank(dto.getUpdatedBy()))
			updatedBy = personService.fetchByUniqueIdOrCreate(dto.getUpdatedBy());
		entity.setUpdatedBy(updatedBy);
		
		Boolean internal = false;
		if (dto.getInternal() != null)
			internal = dto.getInternal();
		entity.setInternal(internal);
		
		Boolean obsolete = false;
		if (dto.getObsolete() != null)
			obsolete = dto.getObsolete();
		entity.setObsolete(obsolete);

		OffsetDateTime dateUpdated = null;
		if (StringUtils.isNotBlank(dto.getDateUpdated())) {
			try {
				dateUpdated = OffsetDateTime.parse(dto.getDateUpdated());
			} catch (DateTimeParseException e) {
				response.addErrorMessage("dateUpdated", ValidationConstants.INVALID_MESSAGE);
			}
		}
		entity.setDateUpdated(dateUpdated);

		OffsetDateTime creationDate = null;
		if (StringUtils.isNotBlank(dto.getDateCreated())) {
			try {
				creationDate = OffsetDateTime.parse(dto.getDateCreated());		
			} catch (DateTimeParseException e) {
				response.addErrorMessage("dateCreated", ValidationConstants.INVALID_MESSAGE);
			}
		}
		entity.setDateCreated(creationDate);
	
		response.setEntity(entity);
		
		return response;
	}
	
	public <E extends BiologicalEntity, D extends BiologicalEntityDTO> ObjectResponse<E> validateBiologicalEntityDTO (E entity, D dto) {
		
		ObjectResponse<E> beResponse = new ObjectResponse<E>();
		
		ObjectResponse<E> aoResponse = validateAuditedObjectDTO(entity, dto);
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
	
	public <E extends GenomicEntity, D extends GenomicEntityDTO> ObjectResponse<E> validateGenomicEntityDTO (E entity, D dto) {
		
		ObjectResponse<E> geResponse = new ObjectResponse<E>();
		
		ObjectResponse<E> beResponse = validateBiologicalEntityDTO(entity, dto);
		geResponse.addErrorMessages(beResponse.getErrorMessages());
		entity = beResponse.getEntity();
		
		if (StringUtils.isNotBlank(dto.getName())) {
			entity.setName(dto.getName());
		} else {
			entity.setName(null);
		}
	
		if (CollectionUtils.isNotEmpty(dto.getSynonyms())) {
			List<Synonym> synonyms = new ArrayList<>();
			for (SynonymDTO synonymDto : dto.getSynonyms()) {
				ObjectResponse<Synonym> synResponse = validateSynonymDTO(synonymDto);
				if (synResponse.hasErrors()) {
					geResponse.addErrorMessage("synonyms", synResponse.errorMessagesString());
				} else {
					synonyms.add(synResponse.getEntity());
				}
			}
		} else {
			entity.setSynonyms(null);
		}
		
		geResponse.setEntity(entity);
		
		return geResponse;
	}
	
	public ObjectResponse<Synonym> validateSynonymDTO(SynonymDTO dto) {
		Synonym synonym = new Synonym();
		ObjectResponse<Synonym> synonymResponse = validateAuditedObjectDTO(synonym, dto);
		synonym = synonymResponse.getEntity();
		
		if (StringUtils.isBlank(dto.getName())) {
			synonymResponse.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			synonym.setName(dto.getName());
		}
		
		synonymResponse.setEntity(synonymDAO.persist(synonym));
		
		return synonymResponse;
	}
}
