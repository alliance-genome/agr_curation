package org.alliancegenome.curation_api.model.ingest.fms.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class CrossReferenceFmsDTO extends BaseDTO {
    private String id;
    private List<String> pages;

    public String getCurie() {
        return id;
    }
}
