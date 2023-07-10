package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.dao.MoleculeDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Molecule;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class MoleculeValidator extends CurieAuditedObjectValidator {

	@Inject
	MoleculeDAO moleculeDAO;

	public Molecule validateMolecule(Molecule uiEntity) {
		response = new ObjectResponse<>(uiEntity);

		String curie = validateCurie(uiEntity);
		if (curie == null) {
			throw new ApiErrorException(response);
		}

		Molecule dbEntity = moleculeDAO.find(curie);
		if (dbEntity == null) {
			addMessageResponse("Could not find Molecule with curie: [" + curie + "]");
			throw new ApiErrorException(response);
		}

		String errorTitle = "Could not update Molecule: [" + curie + "]";

		dbEntity = (Molecule) validateAuditedObjectFields(uiEntity, dbEntity, false);

		dbEntity.setInchi(handleStringField(uiEntity.getInchi()));
		dbEntity.setInchiKey(handleStringField(uiEntity.getInchiKey()));
		dbEntity.setIupac(handleStringField(uiEntity.getIupac()));
		dbEntity.setFormula(handleStringField(uiEntity.getFormula()));
		dbEntity.setSmiles(handleStringField(uiEntity.getSmiles()));

		if (response.hasErrors()) {
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}
}
