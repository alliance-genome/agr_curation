package org.alliancegenome.curation_api.model.dto.json;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class GeneMetaDataDTO extends BaseDTO {

    private MetaDataDTO metaData;
    private List<GeneDTO> data;
}
