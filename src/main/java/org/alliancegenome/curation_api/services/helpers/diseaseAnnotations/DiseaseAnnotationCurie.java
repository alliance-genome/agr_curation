package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.Comparator;

import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.services.CurieGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class DiseaseAnnotationCurie {

    public static final String DELIMITER = "|";

    public abstract String getCurieID(DiseaseModelAnnotationDTO annotationDTO);

    public String getExperimentalConditionCurie(ExperimentalConditionDTO dto) {
        CurieGenerator curie = new CurieGenerator();
        curie.add(dto.getConditionClassId());
        curie.add(dto.getConditionStatement());
        curie.add(dto.getConditionId());
        curie.add(dto.getConditionQuantity());
        return curie.getCurie();
    }

    public String getEvidenceCurie(EvidenceDTO dto) {
        CurieGenerator curie = new CurieGenerator();
        curie.add(dto.getPublication().getPublicationId());
        if (CollectionUtils.isNotEmpty(dto.getEvidenceCodes())) {
            dto.getEvidenceCodes().sort(Comparator.naturalOrder());
            curie.add(StringUtils.join(dto.getEvidenceCodes(), "::"));
        }
        return curie.getCurie();
    }

    public String getAssociationType(DiseaseObjectRelationDTO objectRelation) {
        return objectRelation.getAssociationType();
    }


    public String getPublicationCurie(PublicationDTO dto) {
        CurieGenerator curie = new CurieGenerator();
        // if there is a
        if (dto.getCrossReference() != null) {
            curie.add(dto.getCrossReference().getId());
        } else {
            curie.add(dto.getPublicationId());
        }
        return curie.getCurie();
    }
}
