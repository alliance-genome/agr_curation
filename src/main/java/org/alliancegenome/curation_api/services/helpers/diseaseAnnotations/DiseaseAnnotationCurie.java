package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.EvidenceFmsDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.PublicationFmsDTO;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class DiseaseAnnotationCurie {

	public static final String DELIMITER = "|";

	public static String getConditionRelationUnique(ConditionRelation relation) {
		CurieGeneratorHelper curie = new CurieGeneratorHelper();
		if (relation.getConditionRelationType() != null)
			curie.add(relation.getConditionRelationType().getName());
		curie.add(relation.getHandle());
		if (relation.getSingleReference() != null)
			curie.add(relation.getSingleReference().getCurie());
		if (CollectionUtils.isNotEmpty(relation.getConditions()))
			relation.getConditions().forEach(experimentalCondition -> curie.add(getExperimentalConditionCurie(experimentalCondition)));
		return curie.getCurie();
	}

	public abstract String getCurieID(DiseaseAnnotationDTO annotationDTO);

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
						gen.add(condition.getConditionRelationType().getName());
						condition.getConditions().forEach(cond -> {
							gen.add(getExperimentalConditionCurie(cond));
						});
						return gen.getCurie();
					}).collect(Collectors.joining(DELIMITER))
			);
		}
		return curie.getCurie();
	}

	public static String getExperimentalConditionCurie(ExperimentalCondition cond) {
		CurieGeneratorHelper help = new CurieGeneratorHelper();
		if (cond.getConditionStatement() != null)
			help.add(cond.getConditionStatement());
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
		if (cond.getConditionQuantity() != null)
			help.add(cond.getConditionQuantity());
		return help.getCurie();
	}

	public static String getExperimentalConditionCurie(ExperimentalConditionDTO dto) {
		CurieGeneratorHelper curie = new CurieGeneratorHelper();
		curie.add(dto.getConditionStatement());
		curie.add(dto.getConditionClass());
		curie.add(dto.getConditionId());
		curie.add(dto.getConditionAnatomy());
		curie.add(dto.getConditionChemical());
		curie.add(dto.getConditionGeneOntology());
		curie.add(dto.getConditionTaxon());
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
