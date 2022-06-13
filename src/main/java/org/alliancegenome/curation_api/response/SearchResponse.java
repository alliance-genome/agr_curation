package org.alliancegenome.curation_api.response;

import java.util.*;

import org.alliancegenome.curation_api.view.View;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@Schema(name = "SearchResponse", description = "POJO that represents the SearchResponse")
public class SearchResponse<E> extends APIResponse {

	@JsonView({View.FieldsOnly.class})
	private List<E> results = new ArrayList<E>();

	@JsonView({View.FieldsOnly.class})
	private Long totalResults;

	@JsonView({View.FieldsOnly.class})
	private Integer returnedRecords;

	@JsonView({View.FieldsOnly.class})
	private Map<String, Map<String, Long>> aggregations;
	
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
			this.results = new ArrayList<E>();
		}
	}

	public E getSingleResult() {
		return (results == null || CollectionUtils.isEmpty(results)) ? null : results.get(0);
	}

}
