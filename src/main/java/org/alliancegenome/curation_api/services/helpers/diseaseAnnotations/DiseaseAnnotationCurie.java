package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class DiseaseAnnotationCurie {

    public static final String DELIMITER = "|";

    public abstract String getCurieID(DiseaseModelAnnotationFmsDTO annotationDTO);

    /**
     * @return curie string
     */
    public String getCurieID(String subject, String object, String reference, List<String> evidenceCodes, List<String> conditions ) {
        CurieGeneratorHelper curie = new CurieGeneratorHelper();
        curie.add(subject);
        curie.add(object);
        curie.add(getEvidenceCurie(evidenceCodes, reference));
/*
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
*/
        return curie.getCurie();
    }

    public String getExperimentalConditionCurie(ExperimentalConditionFmsDTO dto) {
        CurieGeneratorHelper curie = new CurieGeneratorHelper();
        curie.add(dto.getConditionClassId());
        curie.add(dto.getConditionStatement());
        curie.add(dto.getConditionId());
        curie.add(dto.getConditionQuantity());
        return curie.getCurie();
    }

    public String getEvidenceCurie(EvidenceFmsDTO dto) {
        CurieGeneratorHelper curie = new CurieGeneratorHelper();

        if(dto.getPublication().getCrossReference() != null){
            curie.add(dto.getPublication().getCrossReference().getCurie());
        }else{
            curie.add(dto.getPublication().getPublicationId());
        }

        if (CollectionUtils.isNotEmpty(dto.getEvidenceCodes())) {
            dto.getEvidenceCodes().sort(Comparator.naturalOrder());
            curie.add(StringUtils.join(dto.getEvidenceCodes(), "::"));
        }
        return curie.getCurie();
    }

    public String getEvidenceCurie(List<String> codes, String reference) {
        CurieGeneratorHelper curie = new CurieGeneratorHelper();

        if(reference != null){
            curie.add(reference);
        }

        if (CollectionUtils.isNotEmpty(codes)) {
            codes.sort(Comparator.naturalOrder());
            curie.add(StringUtils.join(codes, "::"));
        }
        return curie.getCurie();
    }

    public String getAssociationType(DiseaseObjectRelationFmsDTO objectRelation) {
        return objectRelation.getAssociationType();
    }


    public String getPublicationCurie(PublicationFmsDTO dto) {
        CurieGeneratorHelper curie = new CurieGeneratorHelper();
        // if there is a MOD ID
        if (dto.getCrossReference() != null) {
            curie.add(dto.getCrossReference().getId());
        } else {
            curie.add(dto.getPublicationId());
        }
        return curie.getCurie();
    }
}
