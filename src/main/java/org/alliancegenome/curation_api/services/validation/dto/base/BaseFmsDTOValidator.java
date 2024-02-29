package org.alliancegenome.curation_api.services.validation.dto.base;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class BaseFmsDTOValidator {

	@Inject
	OrganizationService organizationService;
	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;
	
	public DataProvider constructDataProvider(BackendBulkDataProvider beDataProvider) {
		ObjectResponse<Organization> orgResponse = organizationService.getByAbbr(beDataProvider.sourceOrganization);
		if (orgResponse == null || orgResponse.getEntity() == null)
			return null;
		
		DataProvider dataProvider = new DataProvider();
		dataProvider.setSourceOrganization(orgResponse.getEntity());
		
		CrossReference homepageXref = new CrossReference();
		homepageXref.setDisplayName(beDataProvider.sourceOrganization);
		homepageXref.setResourceDescriptorPage(resourceDescriptorPageService.getPageForResourceDescriptor(beDataProvider.resourceDescriptor, "homepage"));
		dataProvider.setCrossReference(homepageXref);
		
		return dataProvider;
	}
}
