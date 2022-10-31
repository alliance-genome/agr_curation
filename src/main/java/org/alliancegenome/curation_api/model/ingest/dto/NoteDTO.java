package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Setter
@Getter
public class NoteDTO extends AuditedObjectDTO{
	
	@JsonView({View.FieldsOnly.class})
	private Boolean internal = true;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("free_text")
	private String freeText;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("note_type_name")
	private String noteTypeName;
	
	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("evidence_curies")
	private List<String> evidenceCuries;
}
