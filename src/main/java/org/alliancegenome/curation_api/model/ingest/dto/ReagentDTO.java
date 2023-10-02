package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min = "1.10.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class, DataProviderDTO.class })
public class ReagentDTO extends AuditedObjectDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("mod_entity_id")
	private String modEntityId;
	
	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("mod_internal_id")
	private String modInternalId;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("data_provider_dto")
	private DataProviderDTO dataProviderDto;
	
	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("secondary_identifiers")
	private List<String> secondaryIdentifiers;
}
