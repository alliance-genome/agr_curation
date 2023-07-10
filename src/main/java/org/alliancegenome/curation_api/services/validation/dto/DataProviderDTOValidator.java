package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
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
	CrossReferenceDAO crossReferenceDAO;
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

		String dbXrefUniqueId = null;
		String newXrefUniqueId = null;
		Long dbXrefId = null;
		if (dbEntity.getCrossReference() != null) {
			dbXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(dbEntity.getCrossReference());
			dbXrefId = dbEntity.getCrossReference().getId();
		}
		
		if (dto.getCrossReferenceDto() != null) {
			ObjectResponse<CrossReference> crResponse = crossReferenceDtoValidator.validateCrossReferenceDTO(dto.getCrossReferenceDto());
			if (crResponse.hasErrors()) {
				dpResponse.addErrorMessage("cross_reference_dto", crResponse.errorMessagesString());
			} else {
				newXrefUniqueId = crossReferenceService.getCrossReferenceUniqueId(crResponse.getEntity()); 
				if (dbXrefUniqueId == null || !dbXrefUniqueId.equals(newXrefUniqueId)) {
					dbEntity.setCrossReference(crossReferenceDAO.persist(crResponse.getEntity()));
				} else if (dbXrefUniqueId != null && dbXrefUniqueId.equals(newXrefUniqueId)) {
					dbEntity.setCrossReference(crossReferenceService.updateCrossReference(dbEntity.getCrossReference(), crResponse.getEntity()));
				}
			}
		} else {
			dbEntity.setCrossReference(null);
		}
		
		if (dbXrefId != null && (newXrefUniqueId == null || !dbXrefUniqueId.equals(newXrefUniqueId)) && !dpResponse.hasErrors())
			crossReferenceDAO.remove(dbXrefId);

		dpResponse.setEntity(dbEntity);

		return dpResponse;
	}

}
