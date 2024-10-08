package org.alliancegenome.curation_api.services;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.DataProviderValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RequestScoped
public class DataProviderService extends BaseEntityCrudService<DataProvider, DataProviderDAO> {

	@Inject
	@AuthenticatedUser
	protected Person authenticatedPerson;
	@Inject
	PersonService personService;
	@Inject
	DataProviderDAO dataProviderDAO;
	@Inject
	OrganizationDAO organizationDAO;
	@Inject
	DataProviderValidator dataProviderValidator;

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
	public ObjectResponse<DataProvider> upsert(DataProvider entity) {
		DataProvider dbEntity = getDataProvider(entity.getSourceOrganization(), entity.getCrossReference().getReferencedCurie(), entity.getCrossReference().getResourceDescriptorPage());
		//ObjectResponse<DataProvider> response = dataProviderValidator.validateDataProvider(entity);
		if (dbEntity == null) {
			dataProviderDAO.persist(entity);
			return new ObjectResponse<>(entity);
		}
		return new ObjectResponse<>(dbEntity);
	}

	// <crossReference.referencedCurie, DataProvider>
	HashMap<String, DataProvider> dataProviderMap = new HashMap<>();

	private DataProvider getDataProvider(Organization sourceOrganization, String crossReferenceCurie, ResourceDescriptorPage page) {
		if (dataProviderMap.size() > 0) {
			return dataProviderMap.get(crossReferenceCurie);
		}
		populateDataProviderMap(sourceOrganization, page);
		return dataProviderMap.get(crossReferenceCurie);
	}

	private void populateDataProviderMap(Organization sourceOrganization, ResourceDescriptorPage page) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("sourceOrganization.abbreviation", sourceOrganization.getAbbreviation());
		params.put("crossReference.resourceDescriptorPage.name", page.getName());
		List<DataProvider> allOrgProvider = dataProviderDAO.getAllDataProvider(params);
		allOrgProvider.stream()
			.filter(dataProvider -> dataProvider.getCrossReference() != null && Objects.equals(dataProvider.getCrossReference().getResourceDescriptorPage().getId(), page.getId()))
			.forEach(dataProvider -> {
				dataProviderMap.put(dataProvider.getCrossReference().getReferencedCurie(), dataProvider);
			});
	}

	public HashMap<String, DataProvider> getDataProviderMap(Organization sourceOrganization, ResourceDescriptorPage page) {
		if (dataProviderMap.size() > 0) {
			return dataProviderMap;
		}
		populateDataProviderMap(sourceOrganization, page);
		return dataProviderMap;
	}


	public ObjectResponse<DataProvider> validate(DataProvider uiEntity) {
		return dataProviderValidator.validateDataProvider(uiEntity, null, true);
	}


}
