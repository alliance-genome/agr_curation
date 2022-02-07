package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.alliancegenome.curation_api.view.View;

public class PaperHandle {

    @JsonView({View.FieldsOnly.class})
    private Reference reference;

    @JsonView({View.FieldsOnly.class})
    private String handle;

}
