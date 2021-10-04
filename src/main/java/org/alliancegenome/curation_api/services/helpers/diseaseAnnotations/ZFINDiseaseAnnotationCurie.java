package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseModelAnnotationDTO;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;
import org.apache.commons.collections4.CollectionUtils;

public class ZFINDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

    /**
     * fishID + DOID + PubID + Experimental Condition IDs +
     *
     * @param annotationDTO DiseaseModelAnnotationDTO
     * @return curie string
     */
    @Override
    public String getCurieID(DiseaseModelAnnotationDTO annotationDTO) {
        CurieGeneratorHelper curie = new CurieGeneratorHelper();
        curie.add(annotationDTO.getObjectId());
        curie.add(annotationDTO.getDoId());
        curie.add(getEvidenceCurie(annotationDTO.getEvidence()));
        if (CollectionUtils.isNotEmpty(annotationDTO.getConditionRelations())) {
            curie.add(annotationDTO.getConditionRelations().stream()
                    .map(conditionDTO -> {
                        CurieGeneratorHelper gen = new CurieGeneratorHelper();
                        gen.add(conditionDTO.getConditionRelationType());
                        gen.add(conditionDTO.getConditions().stream()
                                .map(this::getExperimentalConditionCurie).collect(Collectors.joining(DELIMITER))
                        );
                        return gen.getCurie();
                    }).collect(Collectors.joining(DELIMITER))
            );
        }
        return curie.getCurie();
    }
}
