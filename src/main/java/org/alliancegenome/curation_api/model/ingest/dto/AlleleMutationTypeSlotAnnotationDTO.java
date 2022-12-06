package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min="1.3.3", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={SlotAnnotationDTO.class})
public class AlleleMutationTypeSlotAnnotationDTO extends SlotAnnotationDTO {

	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("mutation_type_curies")
	private List<String> mutationTypeCuries;

}
