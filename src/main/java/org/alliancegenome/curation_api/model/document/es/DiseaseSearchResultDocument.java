package org.alliancegenome.curation_api.model.document.es;

import java.util.HashSet;
import java.util.Set;

import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@JsonView(CurationView.DiseaseSearchResultDocument.class)
public class DiseaseSearchResultDocument extends ESDocument {

	{
		category = "disease_search_result";
		searchable = true;
	}

	private String curie;
	private String definition;
	String name;
	Set<String> synonyms = new HashSet<>();
	String primaryKey;
	Set<String> crossReferences = new HashSet<>();
	Set<String> genes = new HashSet<>();
	Set<String> alleles = new HashSet<>();
	Set<String> models = new HashSet<>();
	Set<String> diseaseGroup = new HashSet<>();
	Set<String> associatedSpecies = new HashSet<>();
	String nameKey;

	Set<String> secondaryIds = new HashSet<>();

}
