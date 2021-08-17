package org.alliancegenome.curation_api.model.dto.json;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(hidden = true)
public class AlleleMetaDataDTO extends BaseDTO {

    private MetaDataDTO metaData;
    private List<AlleleDTO> data;
}
