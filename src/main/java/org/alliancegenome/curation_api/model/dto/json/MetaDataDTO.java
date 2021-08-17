package org.alliancegenome.curation_api.model.dto.json;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class MetaDataDTO extends BaseDTO {
    
    private String dateProduced;
    private DataProviderDTO dataProvider;
    private String release;

}
