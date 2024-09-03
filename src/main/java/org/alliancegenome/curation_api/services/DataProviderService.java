package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.model.entities.AllianceMember;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.DataProviderValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class DataProviderService extends BaseEntityCrudService<DataProvider, DataProviderDAO> {

	@Inject
	@AuthenticatedUser protected Person authenticatedPerson;
	@Inject PersonService personService;
	@Inject DataProviderDAO dataProviderDAO;
	@Inject OrganizationDAO organizationDAO;
	@Inject DataProviderValidator dataProviderValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(dataProviderDAO);
	}

	@Transactional
	public DataProvider createAffiliatedModDataProvider() {
		AllianceMember member = authenticatedPerson.getAllianceMember();
		if (member == null) {
			return getAllianceDataProvider();
		} else {
			return dataProviderDAO.getOrCreateDataProvider(member);
		}
	}

	public DataProvider getAllianceDataProvider() {
		return getDefaultDataProvider("Alliance");
	}
	
	@Transactional
	public DataProvider getDefaultDataProvider(String sourceOrganizationAbbreviation) {
		return dataProviderDAO.getOrCreateDataProvider(organizationDAO.getOrCreateOrganization(sourceOrganizationAbbreviation));
	}

	@Transactional
	public ObjectResponse<DataProvider> upsert(DataProvider uiEntity) {
		ObjectResponse<DataProvider> response = dataProviderValidator.validateDataProvider(uiEntity, null, true);
		if (response.getEntity() == null) {
			return response;
		}
		return new ObjectResponse<DataProvider>(response.getEntity());
	}

	public ObjectResponse<DataProvider> validate(DataProvider uiEntity) {
		return dataProviderValidator.validateDataProvider(uiEntity, null, true);
	}


}
