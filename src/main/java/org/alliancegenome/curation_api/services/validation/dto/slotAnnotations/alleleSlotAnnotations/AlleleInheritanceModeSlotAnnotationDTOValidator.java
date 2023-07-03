package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.PhenotypeTermService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleInheritanceModeSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator {

	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	PhenotypeTermService phenotypeTermService;

	public ObjectResponse<AlleleInheritanceModeSlotAnnotation> validateAlleleInheritanceModeSlotAnnotationDTO(AlleleInheritanceModeSlotAnnotation annotation, AlleleInheritanceModeSlotAnnotationDTO dto) {
		ObjectResponse<AlleleInheritanceModeSlotAnnotation> aisaResponse = new ObjectResponse<AlleleInheritanceModeSlotAnnotation>();

		if (annotation == null)
			annotation = new AlleleInheritanceModeSlotAnnotation();

		VocabularyTerm inheritanceMode = null;
		if (StringUtils.isBlank(dto.getInheritanceModeName())) {
			aisaResponse.addErrorMessage("inheritance_mode_name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			inheritanceMode = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY, dto.getInheritanceModeName());
			if (inheritanceMode == null)
				aisaResponse.addErrorMessage("inheritance_mode_name", ValidationConstants.INVALID_MESSAGE);
		}
		annotation.setInheritanceMode(inheritanceMode);
	
		PhenotypeTerm phenotypeTerm = null;
		if (StringUtils.isNotBlank(dto.getPhenotypeTermCurie())) {
			phenotypeTerm = phenotypeTermService.findByCurieOrSecondaryId(dto.getPhenotypeTermCurie());
			if (phenotypeTerm == null)
				aisaResponse.addErrorMessage("phenotype_term_curie", ValidationConstants.INVALID_MESSAGE);
		}
		annotation.setPhenotypeTerm(phenotypeTerm);
		
		String phenotypeStatement = StringUtils.isBlank(dto.getPhenotypeStatement()) ? null : dto.getPhenotypeStatement();
		annotation.setPhenotypeStatement(phenotypeStatement);
		
		ObjectResponse<AlleleInheritanceModeSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		aisaResponse.addErrorMessages(saResponse.getErrorMessages());

		aisaResponse.setEntity(annotation);

		return aisaResponse;
	}
}
