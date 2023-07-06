package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.EvidenceFmsDTO;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class DiseaseAnnotationUniqueIdHelper {

	public static final String DELIMITER = "|";

	public static String getConditionRelationUniqueId(ConditionRelation relation) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		if (relation.getConditionRelationType() != null)
			uniqueId.add(relation.getConditionRelationType().getName());
		uniqueId.add(relation.getHandle());
		if (relation.getSingleReference() != null)
			uniqueId.add(relation.getSingleReference().getCurie());
		if (CollectionUtils.isNotEmpty(relation.getConditions()))
			relation.getConditions().forEach(experimentalCondition -> uniqueId.add(getExperimentalConditionUniqueId(experimentalCondition)));
		return uniqueId.getUniqueId();
	}

	public static String getConditionRelationUniqueId(ConditionRelationDTO dto, String refCurie) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		if (dto.getConditionRelationTypeName() != null)
			uniqueId.add(dto.getConditionRelationTypeName());
		uniqueId.add(dto.getHandle());
		if (refCurie != null)
			uniqueId.add(refCurie);
		if (CollectionUtils.isNotEmpty(dto.getConditionDtos()))
			dto.getConditionDtos().forEach(experimentalCondition -> uniqueId.add(getExperimentalConditionUniqueId(experimentalCondition)));
		return uniqueId.getUniqueId();
	}

	public static String getDiseaseAnnotationUniqueId(DiseaseAnnotationDTO annotationDTO, String subjectCurie, String refCurie) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(subjectCurie);
		uniqueId.add(annotationDTO.getDiseaseRelationName());
		if (annotationDTO.getNegated())
			uniqueId.add(annotationDTO.getNegated().toString());
		uniqueId.add(annotationDTO.getDoTermCurie());
		uniqueId.add(refCurie);
		uniqueId.addList(annotationDTO.getEvidenceCodeCuries());
		uniqueId.addList(annotationDTO.getWithGeneCuries());
		if (CollectionUtils.isNotEmpty(annotationDTO.getConditionRelationDtos())) {
			uniqueId.addList(annotationDTO.getConditionRelationDtos().stream().map(conditionDTO -> {
				UniqueIdGeneratorHelper gen = new UniqueIdGeneratorHelper();
				gen.add(conditionDTO.getConditionRelationTypeName());
				gen.add(conditionDTO.getConditionDtos().stream().map(DiseaseAnnotationUniqueIdHelper::getExperimentalConditionUniqueId).collect(Collectors.joining(DELIMITER)));
				return gen.getUniqueId();
			}).collect(Collectors.toList()));
		}
		uniqueId.addList(annotationDTO.getDiseaseQualifierNames());
		uniqueId.add(annotationDTO.getDiseaseGeneticModifierRelationName());
		uniqueId.addList(annotationDTO.getDiseaseGeneticModifierCuries());
		return uniqueId.getUniqueId();
	}

	public static String getDiseaseAnnotationUniqueId(DiseaseAnnotation annotation) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(annotation.getSubjectCurie());
		if (annotation.getDiseaseRelation() != null)
			uniqueId.add(annotation.getDiseaseRelation().getName());
		if (annotation.getNegated() != null)
			uniqueId.add(annotation.getNegated().toString());
		if (annotation.getObject() != null)
			uniqueId.add(annotation.getObject().getCurie());
		if (annotation.getSingleReference()!= null)
			uniqueId.add(annotation.getSingleReference().getCurie());
		if (CollectionUtils.isNotEmpty(annotation.getEvidenceCodes()))
			uniqueId.addList(annotation.getEvidenceCodes().stream().map(ECOTerm::getCurie).collect(Collectors.toList()));
		if (CollectionUtils.isNotEmpty(annotation.getWith()))
			uniqueId.addList(annotation.getWith().stream().map(Gene::getCurie).collect(Collectors.toList()));
		if (CollectionUtils.isNotEmpty(annotation.getConditionRelations())) {
			uniqueId.addList(annotation.getConditionRelations().stream().map(condition -> {
				UniqueIdGeneratorHelper gen = new UniqueIdGeneratorHelper();
				gen.add(condition.getConditionRelationType().getName());
				gen.add(condition.getConditions().stream().map(DiseaseAnnotationUniqueIdHelper::getExperimentalConditionUniqueId).collect(Collectors.joining(DELIMITER)));
				return gen.getUniqueId();
			}).collect(Collectors.toList()));
		}
		if (CollectionUtils.isNotEmpty(annotation.getDiseaseQualifiers()))
			uniqueId.addList(annotation.getDiseaseQualifiers().stream().map(VocabularyTerm::getName).collect(Collectors.toList()));
		if (annotation.getDiseaseGeneticModifierRelation() != null)
			uniqueId.add(annotation.getDiseaseGeneticModifierRelation().getName());
		if (CollectionUtils.isNotEmpty(annotation.getDiseaseGeneticModifiers()))
			uniqueId.addList(annotation.getDiseaseGeneticModifiers().stream().map(BiologicalEntity::getCurie).collect(Collectors.toList()));
		return uniqueId.getUniqueId();
	}

	public static String getExperimentalConditionUniqueId(ExperimentalCondition cond) {
		UniqueIdGeneratorHelper help = new UniqueIdGeneratorHelper();
		if (cond.getConditionClass() != null)
			help.add(cond.getConditionClass().getCurie());
		if (cond.getConditionId() != null)
			help.add(cond.getConditionId().getCurie());
		if (cond.getConditionAnatomy() != null)
			help.add(cond.getConditionAnatomy().getCurie());
		if (cond.getConditionChemical() != null)
			help.add(cond.getConditionChemical().getCurie());
		if (cond.getConditionGeneOntology() != null)
			help.add(cond.getConditionGeneOntology().getCurie());
		if (cond.getConditionTaxon() != null)
			help.add(cond.getConditionTaxon().getCurie());
		help.add(cond.getConditionQuantity());
		help.add(cond.getConditionFreeText());
		return help.getUniqueId();
	}

	public static String getExperimentalConditionUniqueId(ExperimentalConditionDTO dto) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(dto.getConditionClassCurie());
		uniqueId.add(dto.getConditionIdCurie());
		uniqueId.add(dto.getConditionAnatomyCurie());
		uniqueId.add(dto.getConditionChemicalCurie());
		uniqueId.add(dto.getConditionGeneOntologyCurie());
		uniqueId.add(dto.getConditionTaxonCurie());
		uniqueId.add(dto.getConditionQuantity());
		uniqueId.add(dto.getConditionFreeText());
		return uniqueId.getUniqueId();
	}

	public String getEvidenceCurie(EvidenceFmsDTO dto) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();

		if (dto.getPublication().getCrossReference() != null) {
			uniqueId.add(dto.getPublication().getCrossReference().getCurie());
		} else {
			uniqueId.add(dto.getPublication().getPublicationId());
		}

		if (CollectionUtils.isNotEmpty(dto.getEvidenceCodes())) {
			dto.getEvidenceCodes().sort(Comparator.naturalOrder());
			uniqueId.add(StringUtils.join(dto.getEvidenceCodes(), "::"));
		}
		return uniqueId.getUniqueId();
	}

	public String getEvidenceCurie(List<String> codes, String reference) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();

		if (reference != null) {
			uniqueId.add(reference);
		}

		if (CollectionUtils.isNotEmpty(codes)) {
			codes.sort(Comparator.naturalOrder());
			uniqueId.add(StringUtils.join(codes, "::"));
		}
		return uniqueId.getUniqueId();
	}
}
