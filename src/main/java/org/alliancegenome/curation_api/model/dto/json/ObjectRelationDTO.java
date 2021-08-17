package org.alliancegenome.curation_api.model.dto.json;

import lombok.Data;
import org.alliancegenome.curation_api.base.BaseDTO;

@Data
public class ObjectRelationDTO extends BaseDTO {

    private String associationType;
    private String objectType;
}
