package org.alliancegenome.curation_api.model.ingest.dto;

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
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class })
public class NoteDTO extends AuditedObjectDTO {

	@JsonView({ View.FieldsOnly.class })
	private Boolean internal = true;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("free_text")
	private String freeText;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("note_type_name")
	private String noteTypeName;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("evidence_curies")
	private List<String> evidenceCuries;
}
