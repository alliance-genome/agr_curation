package org.alliancegenome.curation_api.model.ingest.json.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class EvidenceDTO extends BaseDTO {

    private PublicationDTO publication;
    private List<String> evidenceCodes;
}
