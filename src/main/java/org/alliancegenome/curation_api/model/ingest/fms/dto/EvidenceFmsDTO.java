package org.alliancegenome.curation_api.model.ingest.fms.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class EvidenceFmsDTO extends BaseDTO {

    private PublicationFmsDTO publication;
    private List<String> evidenceCodes;

}
