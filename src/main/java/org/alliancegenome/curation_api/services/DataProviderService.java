package org.alliancegenome.curation_api.services;


import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.model.entities.AllianceMember;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.DataProviderValidator;

@RequestScoped
public class DataProviderService extends BaseEntityCrudService<DataProvider, DataProviderDAO> {

	@Inject
	@AuthenticatedUser
	protected LoggedInPerson authenticatedPerson;
	@Inject
	LoggedInPersonService loggedInPersonService;
	@Inject
	DataProviderDAO dataProviderDAO;
	@Inject
	OrganizationDAO organizationDAO;
	@Inject
	DataProviderValidator dataProviderValidator;
	@Inject
	CrossReferenceDAO crossReferenceDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(dataProviderDAO);
	}

	@Transactional
	public DataProvider createAffiliatedModDataProvider() {
		String affiliatedModAbbreviation = null;
		AllianceMember member = authenticatedPerson.getAllianceMember();
		if (authenticatedPerson.getAllianceMember() == null) {
			affiliatedModAbbreviation = "Alliance";
		} else {
			affiliatedModAbbreviation = member.getAbbreviation();
		}
		
		return createDataProvider(affiliatedModAbbreviation);
	}
	
	@Transactional
	public DataProvider createAllianceDataProvider() {
		return createDataProvider("Alliance");
	}
	
	private DataProvider createDataProvider(String abbreviation) {
		DataProvider dataProvider = new DataProvider();
		
		SearchResponse<Organization> orgResponse = organizationDAO.findByField("abbreviation", abbreviation);
		if (orgResponse == null || orgResponse.getSingleResult() == null)
			return null;
		
		Organization sourceOrganization = orgResponse.getSingleResult();
		dataProvider.setSourceOrganization(sourceOrganization);
	
		CrossReference xref = new CrossReference();
		xref.setDisplayName(abbreviation);
		xref.setReferencedCurie(abbreviation);
		xref.setResourceDescriptorPage(sourceOrganization.getHomepageResourceDescriptorPage());
		dataProvider.setCrossReference(crossReferenceDAO.persist(xref));

		return dataProviderDAO.persist(dataProvider);
	}
	
	@Transactional
	public ObjectResponse<DataProvider> upsert(DataProvider uiEntity) {
		ObjectResponse<DataProvider> response = dataProviderValidator.validateDataProvider(uiEntity, null, true);
		if (response.getEntity() == null)
			return response;
		return new ObjectResponse<DataProvider>(dataProviderDAO.persist(response.getEntity()));
	}

	public ObjectResponse<DataProvider> validate(DataProvider uiEntity) {
		return dataProviderValidator.validateDataProvider(uiEntity, null, true);
	}
}
