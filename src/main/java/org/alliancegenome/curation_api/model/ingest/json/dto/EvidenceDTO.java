package org.alliancegenome.curation_api.model.ingest.json.dto;

import lombok.Data;
import org.alliancegenome.curation_api.base.BaseDTO;
import org.alliancegenome.curation_api.services.CurieGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;

@Data
public class EvidenceDTO extends BaseDTO {

    private PublicationDTO publication;
    private List<String> evidenceCodes;

    public String getCurie() {
        CurieGenerator curie = new CurieGenerator();
        curie.add(publication.getPublicationId());
        if (CollectionUtils.isNotEmpty(evidenceCodes)) {
            evidenceCodes.sort(Comparator.naturalOrder());
            curie.add(StringUtils.join(evidenceCodes, "::"));
        }
        return curie.getCurie();
    }
}
