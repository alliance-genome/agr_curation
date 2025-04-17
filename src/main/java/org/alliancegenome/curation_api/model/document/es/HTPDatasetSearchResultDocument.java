package org.alliancegenome.curation_api.model.document.es;

import java.util.Set;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Data;

@Data
@JsonView(View.HTPDatasetSearchResultDocument.class)
public class HTPDatasetSearchResultDocument extends ESDocument {

	{
		category = "htp_dataset_search_result";
	}

	private String dataProvider;
	private String name;
	private String curie;
	private String species;
	private String summary;
	private String href;
	@JsonbProperty("name_key")
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
