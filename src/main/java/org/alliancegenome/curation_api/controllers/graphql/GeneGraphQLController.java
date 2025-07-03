package org.alliancegenome.curation_api.controllers.graphql;

import java.util.HashMap;
import java.util.Map;

import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GeneService;
import org.apache.logging.log4j.plugins.Namespace;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import jakarta.inject.Inject;

@GraphQLApi
@Namespace("gene")
@Description("Gene operations")
public class GeneGraphQLController {

	@Inject GeneService geneService;
	
	@Query
	public ObjectResponse<Gene> geneByIdentifier(String geneId) {
		return geneService.getByIdentifier(geneId);
	}

	@Query
	public SearchResponse<Gene> findGeneByParams(Integer page, Integer limit, Map<String, String> params) {
		Pagination pagination = new Pagination(page, limit);
		HashMap<String, Object> params2 = new HashMap<String, Object>(params);
		return geneService.findByParams(pagination, params2);
	}

}