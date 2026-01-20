package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@AGRCurationSchemaVersion(min = "1.5.1", max = LinkMLSchemaConstants.LATEST_RELEASE)
public class ResourceDescriptorPageDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	private String name;

	@JsonView({ CurationView.FieldsOnly.class })
	private String url;
	
	@JsonView({ CurationView.FieldsOnly.class })
	private String description;

}
