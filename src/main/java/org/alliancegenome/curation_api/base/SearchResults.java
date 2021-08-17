package org.alliancegenome.curation_api.base;

import java.util.List;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class SearchResults<E> {

    @JsonView(View.FieldsOnly.class)
    private Long totalResults;
    @JsonView(View.FieldsOnly.class)
    private List<E> results;
}
