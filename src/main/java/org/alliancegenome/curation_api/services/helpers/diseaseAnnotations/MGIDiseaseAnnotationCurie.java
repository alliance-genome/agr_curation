package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;

public class MGIDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

	/**
	 * genotype ID + DOID + PubID
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
		return curie.getCurie();
	}

	@Override
	public String getCurieID(DiseaseAnnotation annotation) {
		CurieGeneratorHelper curie = new CurieGeneratorHelper();
		curie.add(annotation.getSubjectCurie());
		curie.add(annotation.getObject().getCurie());
		curie.add(annotation.getSingleReference().getCurie());
		return curie.getCurie();
	}

	@Override
	public String getCurieID(String subject, String object, String reference, List<String> evidenceCodes, List<ConditionRelation> relations, String associationType, String negated, String diseaseGeneticModifierRelation, String diseaseGeneticModifier) {
		return super.getCurieID(subject, object, reference, null, null, null, null, null, null);
	}

}
