package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Reagent;
import org.alliancegenome.curation_api.model.ingest.dto.ReagentDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ReagentDTOValidator extends AnnotationDTOValidator {

	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	@Inject
	DataProviderDTOValidator dataProviderDtoValidator;

	public <E extends Reagent, D extends ReagentDTO> ObjectResponse<E> validateReagentDTO(E reagent, D dto) {
		ObjectResponse<E> reagentResponse = validateAuditedObjectDTO(reagent, dto);
		reagent = reagentResponse.getEntity();

		if (StringUtils.isNotBlank(dto.getModEntityId())) {
			reagent.setModEntityId(dto.getModEntityId());
		} else {
			reagent.setModEntityId(null);
		}
		
		if (StringUtils.isNotBlank(dto.getModInternalId())) {
			reagent.setModInternalId(dto.getModInternalId());
		} else {
			reagent.setModInternalId(null);
		}
		
		if (CollectionUtils.isNotEmpty(dto.getSecondaryIdentifiers())) {
			reagent.setSecondaryIdentifiers(dto.getSecondaryIdentifiers());
		} else {
			reagent.setSecondaryIdentifiers(null);
		}
		
		if (dto.getDataProviderDto() == null) {
			reagentResponse.addErrorMessage("data_provider_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<DataProvider> dpResponse = dataProviderDtoValidator.validateDataProviderDTO(dto.getDataProviderDto(), reagent.getDataProvider());
			if (dpResponse.hasErrors()) {
				reagentResponse.addErrorMessage("data_provider_dto", dpResponse.errorMessagesString());
			} else {
				reagent.setDataProvider(dataProviderDAO.persist(dpResponse.getEntity()));
			}
		}

		reagentResponse.setEntity(reagent);
		
		return reagentResponse;
	}
}
