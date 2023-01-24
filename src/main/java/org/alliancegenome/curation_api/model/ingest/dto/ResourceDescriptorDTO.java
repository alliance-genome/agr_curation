package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min = "1.5.1", max = LinkMLSchemaConstants.LATEST_RELEASE)
public class ResourceDescriptorDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("db_prefix")
	private String dbPrefix;

	@JsonView({ View.FieldsOnly.class })
	private String name;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("example_id")
	private String exampleId;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("example_gid")
	private String exampleGid;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("gid_pattern")
	private String gidPattern;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("default_url")
	private String defaultUrl;

	@JsonView({ View.FieldsAndLists.class })
	private List<String> aliases;
	
	@JsonView({ View.FieldsAndLists.class })
	private List<ResourceDescriptorPageDTO> pages;

}
