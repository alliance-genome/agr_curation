package org.alliancegenome.curation_api.model.ingest.fms.dto;

import java.util.Map;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class AlleleObjectRelationsFmsDTO extends BaseDTO {

    private Map<String, String> objectRelation;
}
