package org.alliancegenome.curation_api.model.dto.json;

import org.alliancegenome.curation_api.base.BaseDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(hidden = true)
public class DataProviderDTO extends BaseDTO {

    private CrossReferenceDTO crossReference;
    private DataProviderType type;
    
    
    public enum DataProviderType {
        curated, loaded;
    }
}
