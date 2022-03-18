package org.alliancegenome.curation_api.model.ingest.fms.dto;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class AlleleObjectRelationFmsDTO extends BaseDTO {

    private ObjectRelationFmsDTO objectRelation;
}
