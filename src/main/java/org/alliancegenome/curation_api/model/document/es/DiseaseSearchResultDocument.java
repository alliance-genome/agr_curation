package org.alliancegenome.curation_api.model.document.es;

import java.util.HashSet;
import java.util.Set;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@JsonView(View.DiseaseSearchResultDocument.class)
public class DiseaseSearchResultDocument extends ESDocument {

	{
		category = "disease_search_result";
	}

	private String curie;
	private String definition;
	String name;
	Set<String> synonyms;
	String primaryKey;
	Set<String> crossReferences;
	Set<String> parentDiseaseNames;
	Set<String> genes;
	Set<String> diseaseGroup;
	Set<String> associatedSpecies = new HashSet<>();
	@JsonProperty("name_key")
	String nameKey;

	Set<String> models = new HashSet<>();
	Set<String> alleles = new HashSet<>();

	Set<String> secondaryIds = new HashSet<>();


}
