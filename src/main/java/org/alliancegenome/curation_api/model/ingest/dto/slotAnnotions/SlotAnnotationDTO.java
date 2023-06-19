package org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions;

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
@AGRCurationSchemaVersion(min = "1.3.3", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class })
public class SlotAnnotationDTO extends AuditedObjectDTO {

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("evidence_curies")
	private List<String> evidenceCuries;

}
