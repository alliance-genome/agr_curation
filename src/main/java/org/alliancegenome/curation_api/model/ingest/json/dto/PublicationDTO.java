package org.alliancegenome.curation_api.model.ingest.json.dto;

import lombok.Data;
import org.alliancegenome.curation_api.base.BaseDTO;
import org.alliancegenome.curation_api.services.CurieGenerator;

@Data
public class PublicationDTO extends BaseDTO {

    private String publicationId;
    private CrossReferenceDTO crossReference;

    public String getCurie() {
        CurieGenerator curie = new CurieGenerator();
        curie.add(publicationId);
        if (crossReference != null) {
            curie.add(crossReference.getId());
        }
        return curie.getCurie();
    }

}
