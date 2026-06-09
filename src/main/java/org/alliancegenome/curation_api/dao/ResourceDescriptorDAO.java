package org.alliancegenome.curation_api.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.response.SearchResponse;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ResourceDescriptorDAO extends BaseSQLDAO<ResourceDescriptor> {

	protected ResourceDescriptorDAO() {
		super(ResourceDescriptor.class);
	}

	public List<String> findAllNames() {
		SearchResponse<ResourceDescriptor> response = findAll();
		List<ResourceDescriptor> resourceDescriptors = response.getResults();
		List<String> resourceDescriptorNames = resourceDescriptors.stream().map(ResourceDescriptor::getName).collect(Collectors.toList());

		return resourceDescriptorNames;
	}

	public SearchResponse<ResourceDescriptor> findAllWithPages() {
		List<ResourceDescriptor> results = entityManager
			.createQuery(
				"SELECT DISTINCT rd FROM ResourceDescriptor rd "
					+ "LEFT JOIN FETCH rd.resourcePages "
					+ "WHERE rd.internal = false AND rd.obsolete = false "
					+ "ORDER BY rd.id",
				ResourceDescriptor.class)
			.getResultList();
		// Filter obsolete/internal pages here rather than in JPQL — Hibernate 6
		// rejects WITH on fetch joins. The ResourceDescriptorDocument view does not
		// expose internal/obsolete on pages, so consumers cannot filter downstream.
		for (ResourceDescriptor descriptor : results) {
			if (descriptor.getResourcePages() != null) {
				descriptor.getResourcePages().removeIf(page ->
					Boolean.TRUE.equals(page.getInternal()) || Boolean.TRUE.equals(page.getObsolete()));
			}
		}
		SearchResponse<ResourceDescriptor> response = new SearchResponse<>(results);
		response.setTotalResults((long) results.size());
		return response;
	}

}