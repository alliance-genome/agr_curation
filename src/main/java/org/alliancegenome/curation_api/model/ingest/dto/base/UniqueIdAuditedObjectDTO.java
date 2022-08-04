package org.alliancegenome.curation_api.model.ingest.dto.base;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Setter
@Getter
public class UniqueIdAuditedObjectDTO extends AuditedObjectDTO {
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("unique_id")
	private String uniqueId;
	
}
