package org.alliancegenome.curation_api.controllers.graphql;

import java.util.HashMap;
import java.util.Map;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.apache.logging.log4j.plugins.Namespace;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import jakarta.inject.Inject;

@GraphQLApi
@Namespace("allele")
@Description("Allele operations")
public class AlleleGraphQLController {
	
	@Inject
	AlleleService alleleService;

	@Query
	public ObjectResponse<Allele> alleleByIdentifier(String alleleId) {
		return alleleService.getByIdentifier(alleleId);
	}

	@Query
	public SearchResponse<Allele> findAlleleByParams(Integer page, Integer limit, Map<String, String> params) {
		Pagination pagination = new Pagination(page, limit);
		HashMap<String, Object> params2 = new HashMap<String, Object>(params);
		return alleleService.findByParams(pagination, params2);
	}
}
