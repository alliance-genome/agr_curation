package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleMutationTypeSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class AlleleMutationTypeSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator {
    
	@Inject SoTermDAO soTermDAO;
	
    public ObjectResponse<AlleleMutationTypeSlotAnnotation> validateAlleleMutationTypeSlotAnnotationDTO(AlleleMutationTypeSlotAnnotationDTO dto) {
    	ObjectResponse<AlleleMutationTypeSlotAnnotation> amsaResponse = new ObjectResponse<AlleleMutationTypeSlotAnnotation>();
    	
    	AlleleMutationTypeSlotAnnotation annotation = new AlleleMutationTypeSlotAnnotation();
    	
    	if (CollectionUtils.isEmpty(dto.getMutationTypeCuries())) {
			amsaResponse.addErrorMessage("mutation_type_curies", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			List<SOTerm> soTerms = new ArrayList<>();
			for (String soCurie : dto.getMutationTypeCuries()) {
				SOTerm soTerm = soTermDAO.find(soCurie);
				if (soTerm == null) {
					amsaResponse.addErrorMessage("mutation_type_curies", ValidationConstants.INVALID_MESSAGE + " (" + soCurie + ")");
					break;
				}
				soTerms.add(soTerm);
			}
			annotation.setMutationTypes(soTerms);
		}
    	
    	ObjectResponse<AlleleMutationTypeSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
    	annotation = saResponse.getEntity();
    	amsaResponse.addErrorMessages(saResponse.getErrorMessages());
    	
    	amsaResponse.setEntity(annotation);
    	
    	return amsaResponse;
    }
}
