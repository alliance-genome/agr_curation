package org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** SCRUM-6535: ingest form of the use(s) of a transgenic tool - FBcv 'experimental_tool_descriptor' terms. */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotationDTO.class })
public class TransgenicToolUseSlotAnnotationDTO extends SlotAnnotationDTO {

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("use_curies")
	private List<String> useCuries;

}
