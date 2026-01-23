package org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.5.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotationDTO.class })
public class NameSlotAnnotationDTO extends SlotAnnotationDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("name_type_name")
	private String nameTypeName;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("format_text")
	private String formatText;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("display_text")
	private String displayText;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("synonym_url")
	private String synonymUrl;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("synonym_scope_name")
	private String synonymScopeName;

}
