package org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SlotAnnotationDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min = "1.3.3", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotationDTO.class })
public class AlleleGermlineTransmissionStatusSlotAnnotationDTO extends SlotAnnotationDTO {

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("germline_transmission_status_name")
	private String germlineTransmissionStatusName;

}
