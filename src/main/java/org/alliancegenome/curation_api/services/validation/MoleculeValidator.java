package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.MoleculeDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.base.CurieObjectValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class MoleculeValidator extends CurieObjectValidator {

	@Inject
	MoleculeDAO moleculeDAO;

	private String errorMessage;
	
	public Molecule validateMoleculeUpdate(Molecule uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Molecule: [" + uiEntity.getCurie() + "]";
		
		Long id = uiEntity.getId(); 
		if (id == null) {
			addMessageResponse("No Molecule ID provided");
			throw new ApiErrorException(response);
		}
		
		Molecule dbEntity = moleculeDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("id", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}
		
		dbEntity = (Molecule) validateAuditedObjectFields(uiEntity, dbEntity, false);
		
		return validateMolecule(uiEntity, dbEntity);
	}
	
	public Molecule validateMoleculeCreate(Molecule uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create Molecule";
		
		Molecule dbEntity = new Molecule();
		
		dbEntity = (Molecule) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateMolecule(uiEntity, dbEntity);
	}
	
	public Molecule validateMolecule(Molecule uiEntity, Molecule dbEntity) {
		response = new ObjectResponse<>(uiEntity);

		String curie = validateCurie(uiEntity);
		dbEntity.setCurie(curie);
		
		dbEntity.setInchi(handleStringField(uiEntity.getInchi()));
		dbEntity.setInchiKey(handleStringField(uiEntity.getInchiKey()));
		dbEntity.setIupac(handleStringField(uiEntity.getIupac()));
		dbEntity.setFormula(handleStringField(uiEntity.getFormula()));
		dbEntity.setSmiles(handleStringField(uiEntity.getSmiles()));

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}
}
