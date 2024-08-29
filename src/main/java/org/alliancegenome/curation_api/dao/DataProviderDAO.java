package org.alliancegenome.curation_api.dao;

import java.util.HashMap;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.response.SearchResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DataProviderDAO extends BaseSQLDAO<DataProvider> {

	@Inject CrossReferenceDAO crossReferenceDAO;
	
	private HashMap<String, DataProvider> dataProviderCache = new HashMap<>();

	protected DataProviderDAO() {
		super(DataProvider.class);
	}
	
	@Transactional
	public DataProvider getOrCreateDataProvider(Organization sourceOrganization) {

		if (dataProviderCache.containsKey(sourceOrganization.getAbbreviation())) {
			return dataProviderCache.get(sourceOrganization.getAbbreviation());
		}

		SearchResponse<DataProvider> orgResponse = findByField("sourceOrganization.abbreviation", sourceOrganization.getAbbreviation());
		if (orgResponse != null) {
			DataProvider member = orgResponse.getSingleResult();
			if (member != null && member.getSourceOrganization() != null) {
				return member;
			}
		} else {
			DataProvider dataProvider = new DataProvider();

			dataProvider.setSourceOrganization(sourceOrganization);

			CrossReference xref = new CrossReference();
			xref.setDisplayName(sourceOrganization.getAbbreviation());
			xref.setReferencedCurie(sourceOrganization.getAbbreviation());
			xref.setResourceDescriptorPage(sourceOrganization.getHomepageResourceDescriptorPage());
			dataProvider.setCrossReference(crossReferenceDAO.persist(xref));

			return persist(dataProvider);

		}

		return null;
	}
}
