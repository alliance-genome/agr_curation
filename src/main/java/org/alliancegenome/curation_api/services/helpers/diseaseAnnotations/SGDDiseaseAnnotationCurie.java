package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class SGDDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

	/**
	 * gene ID + DOID + PubID
	 *
	 * @param annotationDTO DiseaseModelAnnotationFmsDTO
	 * @return curie string
	 */
	@Override
	public String getCurieID(DiseaseAnnotationDTO annotationDTO) {
		CurieGeneratorHelper curie = new CurieGeneratorHelper();
		curie.add(annotationDTO.getSubject());
		curie.add(annotationDTO.getObject());
		curie.add(annotationDTO.getSingleReference());
		curie.add(StringUtils.join(annotationDTO.getEvidenceCodes(), "::"));
		curie.add(annotationDTO.getDiseaseRelation());
		curie.add(getWithCuries(annotationDTO));
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
		curie.add(annotation.getDiseaseRelation().getName());
		curie.add(getWithCuries(annotation));
		return curie.getCurie();
	}

	@Override
	public String getCurieID(String subject, String object, String reference, List<String> evidenceCodes, List<ConditionRelation> relations, String associationType) {
		return super.getCurieID(subject, object, reference, null,null, null);
	}

	public String getWithCuries(DiseaseAnnotationDTO annotationDTO) {
		if (CollectionUtils.isEmpty(annotationDTO.getWith()))
			return null;
		CurieGeneratorHelper generator = new CurieGeneratorHelper();
		generator.addAll(annotationDTO.getWith());
		return generator.getCurie();
	}

	public String getWithCuries(DiseaseAnnotation annotation) {
		if (CollectionUtils.isEmpty(annotation.getWith()))
			return null;
		CurieGeneratorHelper generator = new CurieGeneratorHelper();
		generator.addAll(annotation.getWith().stream().map(Gene::getCurie).collect(Collectors.toList()));
		return generator.getCurie();
	}


}

