package org.alliancegenome.curation_api.model.ingest.fms.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class DiseaseAnnotationMetaDataFmsDTO extends BaseDTO {

    private MetaDataFmsDTO metaData;
    private List<DiseaseModelAnnotationFmsDTO> data;
}
