package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseModelAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.services.CurieGenerator;
import org.apache.commons.collections4.CollectionUtils;

import java.util.stream.Collectors;

public class ZFINDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

    /**
     * fishID + DOID + PubID + Experimental Condition IDs +
     *
     * @param annotationDTO DiseaseModelAnnotationDTO
     * @return curie string
     */
    @Override
    public String getCurieID(DiseaseModelAnnotationDTO annotationDTO) {
        CurieGenerator curie = new CurieGenerator();
        curie.add(annotationDTO.getObjectId());
        curie.add(annotationDTO.getDoId());
        curie.add(getEvidenceCurie(annotationDTO.getEvidence()));
        if (CollectionUtils.isNotEmpty(annotationDTO.getConditionRelations())) {
            curie.add(annotationDTO.getConditionRelations().stream()
                    .map(conditionDTO -> {
                        CurieGenerator gen = new CurieGenerator();
                        gen.add(conditionDTO.getConditionRelationType());
                        gen.add(conditionDTO.getConditions().stream()
                                .map(e -> getExperimentalConditionCurie(e)).collect(Collectors.joining(DELIMITER))
                        );
                        return gen.getCurie();
                    }).collect(Collectors.joining(DELIMITER))
            );
        }
        return curie.getCurie();
    }
}
