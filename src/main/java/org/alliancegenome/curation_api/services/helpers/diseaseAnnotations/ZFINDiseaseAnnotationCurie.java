package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class ZFINDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

	/**
	 * fishID + DOID + PubID + Experimental Condition IDs +
	 *
	 * @param annotationDTO DiseaseModelAnnotationFmsDTO
	 * @return curie string
	 */
	
	@Override
	public String getCurieID(DiseaseAnnotationDTO annotationDTO, String subjectCurie, String refCurie) {
		CurieGeneratorHelper curie = new CurieGeneratorHelper();
		curie.add(subjectCurie);
		curie.add(annotationDTO.getDoTermCurie());
		curie.add(refCurie);
		curie.add(StringUtils.join(annotationDTO.getEvidenceCodeCuries(), "::"));

		if(CollectionUtils.isNotEmpty(annotationDTO.getConditionRelationDtos())) {
			curie.add(annotationDTO.getConditionRelationDtos().stream()
				.map(conditionDTO -> {
					CurieGeneratorHelper gen = new CurieGeneratorHelper();
					gen.add(conditionDTO.getConditionRelationTypeName());
					gen.add(conditionDTO.getConditionDtos().stream()
							.map(DiseaseAnnotationCurie::getExperimentalConditionCurie).collect(Collectors.joining(DELIMITER))
					);
					return gen.getCurie();
				}).collect(Collectors.joining(DELIMITER))
			);
		}
		return curie.getCurie();
	}

	@Override
	public String getCurieID(DiseaseAnnotation annotation) {
		CurieGeneratorHelper curie = new CurieGeneratorHelper();
		curie.add(annotation.getSubjectCurie());
		curie.add(annotation.getObject().getCurie());
		curie.add(annotation.getSingleReference().getCurie());
		if (CollectionUtils.isNotEmpty(annotation.getEvidenceCodes()))
			curie.add(StringUtils.join(annotation.getEvidenceCodes().stream().map(ECOTerm::getCurie).collect(Collectors.toList()), "::"));

		if(CollectionUtils.isNotEmpty(annotation.getConditionRelations())) {
			curie.add(annotation.getConditionRelations().stream()
				.map(condition -> {
					CurieGeneratorHelper gen = new CurieGeneratorHelper();
					gen.add(condition.getConditionRelationType().getName());
					gen.add(condition.getConditions().stream()
							.map(DiseaseAnnotationCurie::getExperimentalConditionCurie).collect(Collectors.joining(DELIMITER))
					);
					return gen.getCurie();
				}).collect(Collectors.joining(DELIMITER))
			);
		}
		return curie.getCurie();
	}

	@Override
	public String getCurieID(String subject, String object, String reference, List<String> evidenceCodes, List<ConditionRelation> conditions, String associationType) {
		return super.getCurieID(subject, object, reference, null, conditions, associationType);
	}
}
