package org.alliancegenome.curation_api.model.document.es;

import java.util.Set;

import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@JsonView(CurationView.HTPDatasetSearchResultDocument.class)
public class HTPDatasetSearchResultDocument extends ESDocument {

	{
		category = "htp_dataset_search_result";
		searchable = true;
	}

	private String dataProvider;
	private String name;
	private String curie;
	private String species;
	private String summary;
	private String href;
	private String nameKey;
	private Set<String> tags;
	private Set<String> variantType;
	private Set<String> whereExpressed;
	private Set<String> anatomicalExpression;
	private Set<String> anatomicalExpressionWithParents;
	private Set<String> assays;
	private Set<String> crossReferences;
	private Set<String> sampleIds;
	private Set<String> sex;
}
