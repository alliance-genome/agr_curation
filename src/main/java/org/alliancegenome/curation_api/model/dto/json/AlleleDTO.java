package org.alliancegenome.curation_api.model.dto.json;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(hidden = true)
public class AlleleDTO extends BaseDTO {

    private String primaryId;
    private String symbol;
    private String symbolText;
    private String taxonId;
    private List<String> synonyms;
    private String description;
    private String alleleDescription;
    private List<String> secondaryIds;
    private List<AlleleObjectRelationsDTO> alleleObjectRelations;
    private List<CrossReferenceDTO> crossReferences;
}
