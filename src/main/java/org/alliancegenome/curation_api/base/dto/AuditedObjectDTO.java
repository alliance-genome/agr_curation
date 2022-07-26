package org.alliancegenome.curation_api.base.dto;

import org.alliancegenome.curation_api.base.BaseDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuditedObjectDTO extends BaseDTO {
	
	@JsonView({View.FieldsOnly.class})
	private Boolean internal = false;
	
	@JsonView({View.FieldsOnly.class})
	private Boolean obsolete = false;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("created_by")
	private String createdBy;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("updated_by")
	private String updatedBy;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("date_created")
	private String dateCreated;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("date_updated")
	private String dateUpdated;
	
}
