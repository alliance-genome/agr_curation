package org.alliancegenome.curation_api.model.ingest.fms.dto;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class AffectedGenomicModelComponentFmsDTO extends BaseDTO {

    private String zygosity;
    private String alleleID;
    
}
