package org.alliancegenome.curation_api.model.ingest.fms.dto;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class GeneFmsDTO extends BaseDTO {
    private BasicGeneticEntityFmsDTO basicGeneticEntity;
    private String name;
    private String symbol;
    private String geneSynopsis;
    private String geneSynopsisUrl;
    private String soTermId;
}
