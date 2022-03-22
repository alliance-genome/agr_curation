package org.alliancegenome.curation_api.model.document;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class LiteratureCrossReference {
    @JsonView({View.FieldsOnly.class})
    private String curie;
}
