package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min="1.5.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={SlotAnnotationDTO.class})
public class NameSlotAnnotationDTO extends SlotAnnotationDTO {

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("name_type_name")
	private String nameTypeName;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("format_text")
	private String formatText;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("display_text")
	private String displayText;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("synonym_url")
	private String synonymUrl;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("synonym_scope_name")
	private String synonymScopeName;

}
