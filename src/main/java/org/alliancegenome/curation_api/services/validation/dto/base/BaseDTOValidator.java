package org.alliancegenome.curation_api.services.validation.dto.base;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.enums.SupportedSpecies;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.BiologicalEntityDTO;
import org.alliancegenome.curation_api.model.ingest.dto.GenomicEntityDTO;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.validation.dto.DataProviderDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class BaseDTOValidator {

	@Inject
	PersonService personService;
	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	@Inject
	DataProviderService dataProviderService;
	@Inject
	DataProviderDTOValidator dataProviderDtoValidator;
	@Inject
	DataProviderDAO dataProviderDAO;

	public <E extends AuditedObject, D extends AuditedObjectDTO> ObjectResponse<E> validateAuditedObjectDTO(E entity, D dto) {

		ObjectResponse<E> response = new ObjectResponse<E>();

		Person createdBy = null;
		if (StringUtils.isNotBlank(dto.getCreatedByCurie()))
			createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedByCurie());
		entity.setCreatedBy(createdBy);

		Person updatedBy = null;
		if (StringUtils.isNotBlank(dto.getUpdatedByCurie()))
			updatedBy = personService.fetchByUniqueIdOrCreate(dto.getUpdatedByCurie());
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
				response.addErrorMessage("date_updated", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDateUpdated() + ")");
			}
		}
		entity.setDateUpdated(dateUpdated);

		OffsetDateTime creationDate = null;
		if (StringUtils.isNotBlank(dto.getDateCreated())) {
			try {
				creationDate = OffsetDateTime.parse(dto.getDateCreated());
			} catch (DateTimeParseException e) {
				response.addErrorMessage("date_created", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDateCreated() + ")");
			}
		}
		entity.setDateCreated(creationDate);

		response.setEntity(entity);

		return response;
	}

	public <E extends BiologicalEntity, D extends BiologicalEntityDTO> ObjectResponse<E> validateBiologicalEntityDTO(E entity, D dto) {

		ObjectResponse<E> beResponse = new ObjectResponse<E>();

		ObjectResponse<E> aoResponse = validateAuditedObjectDTO(entity, dto);
		beResponse.addErrorMessages(aoResponse.getErrorMessages());
		entity = aoResponse.getEntity();

		if (StringUtils.isBlank(dto.getTaxonCurie())) {
			beResponse.addErrorMessage("taxon_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.get(dto.getTaxonCurie());
			if (taxonResponse.getEntity() == null) {
				beResponse.addErrorMessage("taxon_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getTaxonCurie() + ")");
			} else {
				NCBITaxonTerm taxon = taxonResponse.getEntity();
				if (!SupportedSpecies.isSupported(taxon.getGenusSpecies())) {
					beResponse.addErrorMessage("taxon_curie", ValidationConstants.UNSUPPORTED_MESSAGE + " (" + dto.getTaxonCurie() + ")");
				}
			}
			entity.setTaxon(taxonResponse.getEntity());
		}
		
		DataProvider dataProvider = null;
		if (dto.getDataProviderDto() == null) {
			beResponse.addErrorMessage("data_provider_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<DataProvider> dpResponse = dataProviderDtoValidator.validateDataProviderDTO(dto.getDataProviderDto(), entity.getDataProvider());
			if (dpResponse.hasErrors()) {
				beResponse.addErrorMessage("data_provider_dto", dpResponse.errorMessagesString());
			} else {
				dataProvider = dataProviderDAO.persist(dpResponse.getEntity());
			}
		}
		entity.setDataProvider(dataProvider);
		
		beResponse.setEntity(entity);

		return beResponse;
	}

	public <E extends GenomicEntity, D extends GenomicEntityDTO> ObjectResponse<E> validateGenomicEntityDTO(E entity, D dto) {

		ObjectResponse<E> geResponse = new ObjectResponse<E>();

		ObjectResponse<E> beResponse = validateBiologicalEntityDTO(entity, dto);
		geResponse.addErrorMessages(beResponse.getErrorMessages());
		entity = beResponse.getEntity();

		geResponse.setEntity(entity);

		return geResponse;
	}
}
