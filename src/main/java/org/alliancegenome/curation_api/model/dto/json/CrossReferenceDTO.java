package org.alliancegenome.curation_api.model.dto.json;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(hidden = true)
public class CrossReferenceDTO extends BaseDTO {
    private String id;
    private List<String> pages;
}
