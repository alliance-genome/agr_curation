package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
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
	
	@Override
	public String getCurieID(String subject, String object, String reference, List<String> evidenceCodes, List<ConditionRelation> relations, String associationType) {
		return super.getCurieID(subject, object, reference, null,null, null);
	}

	@Override
	public String getCurieID(GeneDiseaseAnnotation annotation) {
		return getDiseaseAnnotationCurieID(annotation.getSubject().getCurie(), annotation);
	}

	@Override
	public String getCurieID(AlleleDiseaseAnnotation annotation) {
		return getDiseaseAnnotationCurieID(annotation.getSubject().getCurie(), annotation);
	}

	@Override
	public String getCurieID(AGMDiseaseAnnotation annotation) {
		return getDiseaseAnnotationCurieID(annotation.getSubject().getCurie(), annotation);
	}
		
	private String getDiseaseAnnotationCurieID(String subjectCurie, DiseaseAnnotation annotation) {	
		CurieGeneratorHelper curie = new CurieGeneratorHelper();
		curie.add(subjectCurie);
		curie.add(annotation.getObject().getCurie());
		curie.add(annotation.getSingleReference().getCurie());
		curie.add(StringUtils.join(annotation.getEvidenceCodes().stream().map(EcoTerm::getCurie).collect(Collectors.toList()), "::"));
		curie.add(annotation.getDiseaseRelation().getName());
		curie.add(getWithCuries(annotation));
		
		return curie.getCurie();
	}


}

