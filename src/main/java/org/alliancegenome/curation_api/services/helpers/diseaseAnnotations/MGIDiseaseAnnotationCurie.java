package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseModelAnnotationDTO;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;

public class MGIDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

    /**
     * genotype ID + DOID + PubID
     *
     * @param annotationDTO DiseaseModelAnnotationDTO
     * @return curie string
     */
    @Override
    public String getCurieID(DiseaseModelAnnotationDTO annotationDTO) {
        CurieGeneratorHelper curie = new CurieGeneratorHelper();
        curie.add(annotationDTO.getObjectId());
        curie.add(annotationDTO.getDoId());
        curie.add(getPublicationCurie(annotationDTO.getEvidence().getPublication()));
        return curie.getCurie();
    }
}
