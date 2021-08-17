package org.alliancegenome.curation_api.model.dto.json;

import lombok.Data;
import org.alliancegenome.curation_api.base.BaseDTO;

import java.util.List;

@Data
public class DiseaseAnnotationMetaDataDTO extends BaseDTO {

    private MetaDataDTO metaData;
    private List<DiseaseAnnotationDTO> data;
}
