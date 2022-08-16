package org.alliancegenome.curation_api.model.output;

import java.util.TreeMap;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class APIVersionInfo {
	
	@JsonView(View.FieldsOnly.class)
	private String name;
	@JsonView(View.FieldsOnly.class)
	private String version;
	@JsonView(View.FieldsOnly.class)
	private TreeMap<String, String> agrCurationSchemaVersions;
	@JsonView(View.FieldsOnly.class)
	private String esHost;
	@JsonView(View.FieldsOnly.class)
	private String env;
	
}
