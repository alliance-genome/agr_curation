package org.alliancegenome.curation_api.model.document.es;

import java.util.Set;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Data;

@Data
@JsonView(View.GOSearchResultDocument.class)
public class GOSearchResultDocument extends ESDocument {

	{
		category = "go_search_result";
	}

	private String curie;
	private String branch;
	private String definition;
	private String name;
	private String href;
	@JsonbProperty("name_key")
	private String nameKey;
	private Set<String> associatedSpecies;
	private Set<String> genes;
	private Set<String> synonyms;

}
