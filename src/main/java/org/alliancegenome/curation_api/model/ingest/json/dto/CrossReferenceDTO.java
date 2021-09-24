package org.alliancegenome.curation_api.model.ingest.json.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class CrossReferenceDTO extends BaseDTO {
    private String id;
    private List<String> pages;

    public String getCurie() {
        return id;
    }
}
