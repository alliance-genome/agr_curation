package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseModelAnnotationDTO;

public class WormDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

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
        curie.add(annotationDTO.getEvidence().getPublication().getCurie());
        return curie.getCurie();
    }
}
