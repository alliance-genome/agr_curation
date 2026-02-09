package org.alliancegenome.curation_api.model.output;

import java.util.TreeMap;

import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class APIVersionInfo {

	@JsonView(CurationView.FieldsOnly.class)
	private String name;
	@JsonView(CurationView.FieldsOnly.class)
	private String version;
	@JsonView(CurationView.FieldsOnly.class)
	private TreeMap<String, String> agrCurationSchemaVersions;
	@JsonView(CurationView.FieldsOnly.class)
	private TreeMap<String, String> submittedClassSchemaVersions;
	@JsonView(CurationView.FieldsOnly.class)
	private String esHost;
	@JsonView(CurationView.FieldsOnly.class)
	private String env;
	@JsonView(CurationView.FieldsOnly.class)
	private String matiHost;
}
