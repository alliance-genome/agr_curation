package org.alliancegenome.curation_api.model.document.es;

import java.util.HashSet;
import java.util.Set;

import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@JsonView(CurationView.DiseaseSearchResultDocument.class)
public class DiseaseSearchResultDocument extends ESDocument {

	{
		category = "disease_search_result";
	}

	@JsonView(CurationView.DiseaseSearchResultDocument.class)
	private String curie;
	@JsonView(CurationView.DiseaseSearchResultDocument.class)
	private String definition;
	@JsonView(CurationView.DiseaseSearchResultDocument.class)
	String name;
	@JsonView(CurationView.DiseaseSearchResultDocument.class)
	Set<String> synonyms = new HashSet<>();
	@JsonView(CurationView.DiseaseSearchResultDocument.class)
	String primaryKey;
	@JsonView(CurationView.DiseaseSearchResultDocument.class)
	Set<String> crossReferences = new HashSet<>();
	@JsonView(CurationView.DiseaseSearchResultDocument.class)
	Set<String> genes = new HashSet<>();
	@JsonView(CurationView.DiseaseSearchResultDocument.class)
	Set<String> diseaseGroup = new HashSet<>();
	@JsonView(CurationView.DiseaseSearchResultDocument.class)
	Set<String> associatedSpecies = new HashSet<>();
	@JsonView(CurationView.DiseaseSearchResultDocument.class)
	@JsonProperty("name_key")
	String nameKey;

	@JsonView(CurationView.DiseaseSearchResultDocument.class)
	Set<String> secondaryIds = new HashSet<>();

}
