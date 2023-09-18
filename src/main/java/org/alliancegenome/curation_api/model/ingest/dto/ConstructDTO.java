package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.constructSlotAnnotations.ConstructComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min = "1.9.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { ReagentDTO.class })
public class ConstructDTO extends ReagentDTO {

	@JsonView({ View.FieldsOnly.class })
	private String name;
	
	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("reference_curies")
	private List<String> referenceCuries;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("construct_component_dtos")
	private List<ConstructComponentSlotAnnotationDTO> constructComponentDtos;
}
