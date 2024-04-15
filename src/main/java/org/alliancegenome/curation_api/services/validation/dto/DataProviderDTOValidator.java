package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.ingest.dto.DataProviderDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class DataProviderDTOValidator extends BaseDTOValidator {

	@Inject
	OrganizationService organizationService;
	@Inject
	ResourceDescriptorPageDAO resourceDescriptorPageDAO;
	@Inject
	CrossReferenceService crossReferenceService;
	@Inject
	CrossReferenceDTOValidator crossReferenceDtoValidator;
	
	public ObjectResponse<DataProvider> validateDataProviderDTO(DataProviderDTO dto, DataProvider dbEntity) {
		if (dbEntity == null)
			dbEntity = new DataProvider();
		
		ObjectResponse<DataProvider> dpResponse = validateAuditedObjectDTO(dbEntity, dto);

		dbEntity = dpResponse.getEntity();

		if (StringUtils.isBlank(dto.getSourceOrganizationAbbreviation())) {
			dpResponse.addErrorMessage("source_organization_abbreviation", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<Organization> soResponse = organizationService.getByAbbr(dto.getSourceOrganizationAbbreviation());
			if (soResponse == null || soResponse.getEntity() == null) {
				dpResponse.addErrorMessage("source_organization_abbreviation", ValidationConstants.INVALID_MESSAGE + " (" + dto.getSourceOrganizationAbbreviation() + ")");
			} else {
				dbEntity.setSourceOrganization(soResponse.getEntity());
			}
		}

		if (dto.getCrossReferenceDto() != null) {
			ObjectResponse<CrossReference> crResponse = crossReferenceDtoValidator.validateCrossReferenceDTO(dto.getCrossReferenceDto(), dbEntity.getCrossReference());
			if (crResponse.hasErrors()) {
				dpResponse.addErrorMessage("cross_reference_dto", crResponse.errorMessagesString());
			} else {
				dbEntity.setCrossReference(crResponse.getEntity());
			}
		} else {
			dbEntity.setCrossReference(null);
		}
		dpResponse.setEntity(dbEntity);

		return dpResponse;
	}

}
