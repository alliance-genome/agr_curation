package org.alliancegenome.curation_api.model.dto.json;

import java.util.Map;

import org.alliancegenome.curation_api.base.BaseDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(hidden = true)
public class AlleleObjectRelationsDTO extends BaseDTO {

    private Map<String, String> objectRelation;
}
