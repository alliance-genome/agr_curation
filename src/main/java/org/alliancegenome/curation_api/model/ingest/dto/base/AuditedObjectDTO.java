package org.alliancegenome.curation_api.model.ingest.dto.base;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.GenomicEntityDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Setter
@Getter
@AGRCurationSchemaVersion(min="1.4.0", max=LinkMLSchemaConstants.LATEST_RELEASE)
public class AuditedObjectDTO extends BaseDTO {
	
	@JsonView({View.FieldsOnly.class})
	private Boolean internal = false;
	
	@JsonView({View.FieldsOnly.class})
	private Boolean obsolete = false;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("created_by_curie")
	private String createdByCurie;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("updated_by_curie")
	private String updatedByCurie;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("date_created")
	private String dateCreated;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("date_updated")
	private String dateUpdated;
	
}
