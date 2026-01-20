package org.alliancegenome.curation_api.model.ingest.dto.base;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE)
public class AuditedObjectDTO extends BaseDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	private Boolean internal = false;

	@JsonView({ CurationView.FieldsOnly.class })
	private Boolean obsolete = false;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("created_by_curie")
	private String createdByCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("updated_by_curie")
	private String updatedByCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("date_created")
	private String dateCreated;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("date_updated")
	private String dateUpdated;

}
