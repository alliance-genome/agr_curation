package org.alliancegenome.curation_api.model.dto.json;

import lombok.Data;
import org.alliancegenome.curation_api.base.BaseDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Data
@Schema(hidden = true)
public class DiseaseAnnotationMetaDataDTO extends BaseDTO {

    private MetaDataDTO metaData;
    private List<DiseaseModelAnnotationDTO> data;
}
