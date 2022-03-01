package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Setter
@Getter
public class NoteDTO {
    
    @JsonView({View.FieldsOnly.class})
    private Boolean internal;
    
    @JsonView({View.FieldsOnly.class})
    @JsonProperty("free_text")
    private String freeText;
    
    @JsonView({View.FieldsOnly.class})
    @JsonProperty("note_type")
    private String noteType;
    
    @JsonView({View.FieldsAndLists.class})
    private List<String> references;
}
