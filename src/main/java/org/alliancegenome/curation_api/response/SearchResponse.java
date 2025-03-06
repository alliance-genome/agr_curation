package org.alliancegenome.curation_api.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AllianceMember;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.view.View;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@Schema(name = "SearchResponse", description = "POJO that represents the SearchResponse")
@JsonView({ View.FieldsOnly.class, View.ForPublic.class, View.GeneToGeneOrthologyForIndexer.class })
public class SearchResponse<E> extends APIResponse {

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonSubTypes({
		@Type(value = GeneDiseaseAnnotation.class, name = "GeneDiseaseAnnotation"),
		@Type(value = AlleleDiseaseAnnotation.class, name = "AlleleDiseaseAnnotation"),
		@Type(value = AGMDiseaseAnnotation.class, name = "AGMDiseaseAnnotation"),
		@Type(value = AllianceMember.class, name = "AllianceMember")
	})
	private List<E> results = new ArrayList<E>();

	private Long totalResults;
	private Integer returnedRecords;
	private Map<String, Map<String, Long>> aggregations;
	private String debug;
	private String esQuery;
	private String dbQuery;

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

	@JsonIgnore
	public E getSingleResult() {
		return (results == null || CollectionUtils.isEmpty(results)) ? null : results.get(0);
	}

}
