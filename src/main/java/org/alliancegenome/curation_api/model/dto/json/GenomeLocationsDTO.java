package org.alliancegenome.curation_api.model.dto.json;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class GenomeLocationsDTO extends BaseDTO {

    private String assembly;
    private Number startPosition;
    private Number endPosition;
    private String chromosome;
    private String strand;

}
