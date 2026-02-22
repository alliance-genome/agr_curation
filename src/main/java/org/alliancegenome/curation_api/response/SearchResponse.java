package org.alliancegenome.curation_api.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.view.CurationView;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@Schema(name = "SearchResponse", description = "SearchResponse: wraps paginated search results with metadata. The 'results' field contains matching entities, with totalResults, returnedRecords, and aggregations for pagination.")
@JsonView({
	CurationView.FieldsOnly.class,
	CurationView.ForPublic.class,
	CurationView.GeneToGeneOrthologyDocument.class,
	CurationView.GOSearchResultDocument.class,
	CurationView.GeneSummaryDocument.class,
	CurationView.GeneSearchResultDocument.class,
	CurationView.DiseaseSummaryDocument.class,
	CurationView.DiseaseSearchResultDocument.class,
	CurationView.AlleleSummaryDocument.class,
	CurationView.ModelDocument.class,
	CurationView.VariantSummaryDocument.class,
	CurationView.SequenceSummaryDocument.class,
	CurationView.HTPDatasetSearchResultDocument.class,
	CurationView.GeneExpressionDocument.class
})
public class SearchResponse<E> extends APIResponse {

	@Schema(description = "The list of matching entity objects")
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
	private List<E> results = new ArrayList<>();

	@Schema(description = "Total number of matching results across all pages")
	private long totalResults;
	@Schema(description = "Number of records returned in this page")
	private Integer returnedRecords;
	@Schema(description = "Faceted aggregation counts keyed by field name")
	private Map<String, Map<String, Long>> aggregations;
	@Schema(description = "Debug information for the search query")
	private String debug;
	@Schema(description = "The generated Elasticsearch/OpenSearch query")
	private String esQuery;
	@Schema(description = "The generated database query")
	private String dbQuery;
	@Schema(description = "Cursor value for fetching the next page of results")
	private long nextCursor;


	public SearchResponse() {
	}

	public SearchResponse(List<E> results) {
		setResults(results);
	}

	public void setResults(List<E> results) {
		this.results = results;
		if (results != null) {
			returnedRecords = results.size();
		} else {
			this.results = new ArrayList<>();
		}
	}

	@JsonIgnore
	public E getSingleResult() {
		return (results == null || CollectionUtils.isEmpty(results)) ? null : results.get(0);
	}

}
