package org.alliancegenome.curation_api.model.output;

import java.util.Map;

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
	private Map<String, String> linkMLClassVersions;
	@JsonView(View.FieldsOnly.class)
	private String esHost;
	@JsonView(View.FieldsOnly.class)
	private String env;
	
}
