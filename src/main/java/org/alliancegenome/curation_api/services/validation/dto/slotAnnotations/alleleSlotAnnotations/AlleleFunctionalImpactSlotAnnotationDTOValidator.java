package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.ontology.PhenotypeTermService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleFunctionalImpactSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator {

	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	PhenotypeTermService phenotypeTermService;

	public ObjectResponse<AlleleFunctionalImpactSlotAnnotation> validateAlleleFunctionalImpactSlotAnnotationDTO(AlleleFunctionalImpactSlotAnnotation annotation, AlleleFunctionalImpactSlotAnnotationDTO dto) {
		ObjectResponse<AlleleFunctionalImpactSlotAnnotation> afisaResponse = new ObjectResponse<AlleleFunctionalImpactSlotAnnotation>();

		if (annotation == null)
			annotation = new AlleleFunctionalImpactSlotAnnotation();

		if (CollectionUtils.isEmpty(dto.getFunctionalImpactNames())) {
			afisaResponse.addErrorMessage("functional_impact_names", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			List<VocabularyTerm> functionalImpacts = new ArrayList<>();
			for (String fiName : dto.getFunctionalImpactNames()) {
				VocabularyTerm functionalImpact = vocabularyTermService.getTermInVocabulary(VocabularyConstants.ALLELE_FUNCTIONAL_IMPACT_VOCABULARY, fiName).getEntity();
				if (functionalImpact == null) {
					afisaResponse.addErrorMessage("functional_impact_names", ValidationConstants.INVALID_MESSAGE);
					break;
				}	
				functionalImpacts.add(functionalImpact);
			}
			annotation.setFunctionalImpacts(functionalImpacts);
		}
	
		PhenotypeTerm phenotypeTerm = null;
		if (StringUtils.isNotBlank(dto.getPhenotypeTermCurie())) {
			phenotypeTerm = phenotypeTermService.findByCurieOrSecondaryId(dto.getPhenotypeTermCurie());
			if (phenotypeTerm == null)
				afisaResponse.addErrorMessage("phenotype_term_curie", ValidationConstants.INVALID_MESSAGE);
		}
		annotation.setPhenotypeTerm(phenotypeTerm);
		
		String phenotypeStatement = StringUtils.isBlank(dto.getPhenotypeStatement()) ? null : dto.getPhenotypeStatement();
		annotation.setPhenotypeStatement(phenotypeStatement);
		
		ObjectResponse<AlleleFunctionalImpactSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		afisaResponse.addErrorMessages(saResponse.getErrorMessages());

		afisaResponse.setEntity(annotation);

		return afisaResponse;
	}
}
