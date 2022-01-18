package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class DiseaseAnnotationCurie {

    public static final String DELIMITER = "|";

    public abstract String getCurieID(DiseaseModelAnnotationFmsDTO annotationDTO);

    /**
     * @return curie string
     */
    public String getCurieID(
            String subject,
            String object,
            String reference,
            List<String> evidenceCodes,
            List<ConditionRelation> conditionRelations,
            String associationType) {
        CurieGeneratorHelper curie = new CurieGeneratorHelper();
        curie.add(subject);
        curie.add(object);
        if (associationType != null)
            curie.add(associationType);
        curie.add(getEvidenceCurie(evidenceCodes, reference));
        if (CollectionUtils.isNotEmpty(conditionRelations)) {
            curie.add(conditionRelations.stream()
                    .map(condition -> {
                        CurieGeneratorHelper gen = new CurieGeneratorHelper();
                        gen.add(condition.getRelationType());
                        condition.getConditions().forEach(cond -> {
                            CurieGeneratorHelper help = new CurieGeneratorHelper();
                            help.add(cond.getConditionStatement());
                            help.add(cond.getConditionClass().getCurie());
                            help.add(cond.getConditionAnatomy().getCurie());
                            help.add(cond.getConditionChemical().getCurie());
                            help.add(cond.getConditionGO().getCurie());
                            help.add(cond.getConditionTaxon().getCurie());
                            help.add(cond.getConditionQuantity().getCurie());
                            gen.add(help.getCurie());
                        });
                        return gen.getCurie();
                    }).collect(Collectors.joining(DELIMITER))
            );
        }
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

        if (dto.getPublication().getCrossReference() != null) {
            curie.add(dto.getPublication().getCrossReference().getCurie());
        } else {
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

        if (reference != null) {
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
