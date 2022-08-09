package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;

public class WormDiseaseAnnotationCurie extends DiseaseAnnotationCurie {

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
		return curie.getCurie();
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
		
		return curie.getCurie();	
	}
}
