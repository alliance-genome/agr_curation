package org.alliancegenome.curation_api.model.ingest.fms.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class DiseaseObjectRelationFmsDTO extends BaseDTO {

    private String associationType;
    private String objectType;
    private List<String> inferredGeneAssociation;
}
