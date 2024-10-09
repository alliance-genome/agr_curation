package org.alliancegenome.curation_api.services;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.DataProviderValidator;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;
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
		String referencedCurie = entity.getCrossReference().getReferencedCurie();
		// find associated gene
		Long geneID = getAssociatedGeneId(referencedCurie);
		// if no gene found skip (= don't import) the accession
		if (geneID == null) {
			return new ObjectResponse<>();
		}

		DataProvider dbEntity = getDataProvider(entity.getSourceOrganization(), referencedCurie, entity.getCrossReference().getResourceDescriptorPage());
		if (dbEntity == null) {
			dataProviderDAO.persist(entity);
			Integer update = crossReferenceDAO.persistAccessionGeneAssociated(entity.getCrossReference().getId(), geneID);
			return new ObjectResponse<>(entity);
		}
		return new ObjectResponse<>(dbEntity);
	}

	@NotNull
	public static String getFullReferencedCurie(String localReferencedCurie) {
		return RESOURCE_DESCRIPTOR_PREFIX + ":" + localReferencedCurie;
	}

	Map<String, Long> accessionGeneMap = new HashMap<>();
	public static String RESOURCE_DESCRIPTOR_PREFIX = "ENSEMBL";

	private Long getAssociatedGeneId(String fullReferencedCurie) {
		if (accessionGeneMap.size() == 0) {
			ResourceDescriptorPage page = resourceDescriptorPageService.getPageForResourceDescriptor(RESOURCE_DESCRIPTOR_PREFIX, "default");
			accessionGeneMap = crossReferenceDAO.getGenesWithCrossRefs(page);
		}
		return accessionGeneMap.get(fullReferencedCurie);
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
