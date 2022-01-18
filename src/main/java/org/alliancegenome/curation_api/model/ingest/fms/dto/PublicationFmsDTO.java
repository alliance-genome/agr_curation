package org.alliancegenome.curation_api.model.ingest.fms.dto;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class PublicationFmsDTO extends BaseDTO {

    private String publicationId;
    private CrossReferenceFmsDTO crossReference;

}
