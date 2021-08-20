package org.alliancegenome.curation_api.model.ingest.json.dto;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class PublicationDTO extends BaseDTO {

    private String publicationId;
    private CrossReferenceDTO crossReference;
}
