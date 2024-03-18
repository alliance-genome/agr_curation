package org.alliancegenome.curation_api.model.ingest.dto.associations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.9.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class })
public class EvidenceAssociationDTO extends AuditedObjectDTO {

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("evidence_curies")
	private List<String> evidenceCuries;
}
