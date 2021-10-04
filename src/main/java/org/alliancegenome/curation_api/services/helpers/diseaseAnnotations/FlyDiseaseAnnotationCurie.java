package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseModelAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseObjectRelationDTO;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;

public class FlyDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

    /**
     * gene ID + DOID + PubID + association type
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
        curie.add(getAssociationType(annotationDTO.getObjectRelation()));
        return curie.getCurie();
    }

}
