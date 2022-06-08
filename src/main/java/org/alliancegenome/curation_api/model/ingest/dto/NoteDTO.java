package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.dto.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NoteDTO extends AuditedObjectDTO{
	
	@JsonView({View.FieldsOnly.class})
	private Boolean internal = true;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("free_text")
	private String freeText;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("note_type")
	private String noteType;
	
	@JsonView({View.FieldsAndLists.class})
	private List<String> references;
}
