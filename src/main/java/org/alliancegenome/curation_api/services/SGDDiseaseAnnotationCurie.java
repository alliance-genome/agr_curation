package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseModelAnnotationDTO;

public class SGDDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

    /**
     * gene ID + DOID + PubID
     *
     * @param annotationDTO DiseaseModelAnnotationDTO
     * @return curie string
     */
    @Override
    public String getCurieID(DiseaseModelAnnotationDTO annotationDTO) {
        CurieGenerator curie = new CurieGenerator();
        curie.add(annotationDTO.getObjectId());
        curie.add(annotationDTO.getDoId());
        curie.add(annotationDTO.getEvidence().getCurie());
        return curie.getCurie();
    }
}
