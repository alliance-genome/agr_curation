package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.6.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class})
public class CrossReferenceDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("referenced_curie")
	private String referencedCurie;
	
	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("page_area")
	private String pageArea;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("display_name")
	private String displayName;


	@JsonView({ CurationView.FieldsOnly.class })
	private String prefix;
	
}
