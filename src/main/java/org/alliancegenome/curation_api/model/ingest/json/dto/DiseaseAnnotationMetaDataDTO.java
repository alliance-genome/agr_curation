package org.alliancegenome.curation_api.model.ingest.json.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class DiseaseAnnotationMetaDataDTO extends BaseDTO {

    private MetaDataDTO metaData;
    private List<DiseaseModelAnnotationDTO> data;
}
