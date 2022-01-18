package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.ingest.fms.dto.DiseaseModelAnnotationFmsDTO;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class SGDDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

    /**
     * gene ID + DOID + PubID
     *
     * @param annotationDTO DiseaseModelAnnotationFmsDTO
     * @return curie string
     */
    @Override
    public String getCurieID(DiseaseModelAnnotationFmsDTO annotationDTO) {
        CurieGeneratorHelper curie = new CurieGeneratorHelper();
        curie.add(annotationDTO.getObjectId());
        curie.add(annotationDTO.getDoId());
        curie.add(getEvidenceCurie(annotationDTO.getEvidence()));
        curie.add(getWithCuries(annotationDTO));
        return curie.getCurie();
    }

    public String getWithCuries(DiseaseModelAnnotationFmsDTO annotationDTO) {
        if (CollectionUtils.isEmpty(annotationDTO.getWith()))
            return null;
        CurieGeneratorHelper generator = new CurieGeneratorHelper();
        generator.addAll(annotationDTO.getWith());
        return generator.getCurie();
    }

    @Override
    public String getCurieID(String subject, String object, String reference, List<String> evidenceCodes, List<ConditionRelation> relations, String associationType) {
        return super.getCurieID(subject, object, reference, null,null, null);
    }


}

