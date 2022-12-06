package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.NameSlotAnnotationValidator;

@RequestScoped
public class AlleleFullNameSlotAnnotationValidator extends NameSlotAnnotationValidator<AlleleFullNameSlotAnnotation> {

	@Inject
	AlleleFullNameSlotAnnotationDAO alleleFullNameDAO;
	@Inject
	AlleleDAO alleleDAO;

	public ObjectResponse<AlleleFullNameSlotAnnotation> validateAlleleFullNameSlotAnnotation(AlleleFullNameSlotAnnotation uiEntity) {
		AlleleFullNameSlotAnnotation fullName = validateAlleleFullNameSlotAnnotation(uiEntity, false, false);
		response.setEntity(fullName);
		return response;
	}

	public AlleleFullNameSlotAnnotation validateAlleleFullNameSlotAnnotation(AlleleFullNameSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleFullNameSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleFullNameSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleFullNameDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleFullNameSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleFullNameSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (AlleleFullNameSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateFullNameType(uiEntity.getNameType(), dbEntity.getNameType());
		dbEntity.setNameType(nameType);

		if (validateAllele) {
			Allele singleAllele = validateSingleAllele(uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
		}

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorTitle);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}

}
