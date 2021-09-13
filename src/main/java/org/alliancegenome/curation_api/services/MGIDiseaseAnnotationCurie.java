package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseModelAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.ExperimentalConditionDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.stream.Collectors;

public class MGIDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

    /**
     * genotype ID + DOID + PubID
     *
     * @param annotationDTO DiseaseModelAnnotationDTO
     * @return curie string
     */
    @Override
    public String getCurieID(DiseaseModelAnnotationDTO annotationDTO) {
        CurieGenerator curie = new CurieGenerator();
        curie.add(annotationDTO.getObjectId());
        curie.add(annotationDTO.getDoId());
        curie.add(annotationDTO.getEvidence().getPublication().getCurie());
        return curie.getCurie();
    }
}
