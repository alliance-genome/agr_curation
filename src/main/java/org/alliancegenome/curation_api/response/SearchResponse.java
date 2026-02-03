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
@Schema(name = "SearchResponse", description = "POJO that represents the SearchResponse")
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
	CurationView.VariantDocument.class,
	CurationView.HTPDatasetSearchResultDocument.class,
	CurationView.GeneExpressionDocument.class
})
public class SearchResponse<E> extends APIResponse {

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
	private List<E> results = new ArrayList<>();

	private Long totalResults;
	private Integer returnedRecords;
	private Map<String, Map<String, Long>> aggregations;
	private String debug;
	private String esQuery;
	private String dbQuery;
	private Long nextCursor; // For cursor-based pagination

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
