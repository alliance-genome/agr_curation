package org.alliancegenome.curation_api.model.ingest.json.dto;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class AffectedGenomicModelComponentDTO extends BaseDTO {

    private String zygosity;
    private String alleleID;
    
}
