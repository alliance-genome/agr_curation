package org.alliancegenome.curation_api.model.ingest.dto.base;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Setter
@Getter
public class CurieAuditedObjectDTO extends AuditedObjectDTO {
	
	@JsonView({View.FieldsOnly.class})
	private String curie;
	
}
