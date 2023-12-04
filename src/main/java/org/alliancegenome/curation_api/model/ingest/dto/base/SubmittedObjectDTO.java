package org.alliancegenome.curation_api.model.ingest.dto.base;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.DataProviderDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min = "2.0.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class, DataProviderDTO.class })
public class SubmittedObjectDTO extends AuditedObjectDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("mod_entity_id")
	private String modEntityId;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("mod_internal_id")
	private String modInternalId;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("data_provider_dto")
	private DataProviderDTO dataProviderDto;

}
