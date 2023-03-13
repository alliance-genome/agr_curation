package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min = "1.5.1", max = LinkMLSchemaConstants.LATEST_RELEASE)
public class ResourceDescriptorPageDTO {

	@JsonView({ View.FieldsOnly.class })
	private String name;

	@JsonView({ View.FieldsOnly.class })
	private String url;
	
	@JsonView({ View.FieldsOnly.class })
	private String description;

}
