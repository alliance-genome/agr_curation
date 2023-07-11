package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AffectedGenomicModelValidator extends GenomicEntityValidator {

	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	CrossReferenceDAO crossReferenceDAO;
	
	private String errorMessage;

	public AffectedGenomicModel validateAffectedGenomicModelUpdate(AffectedGenomicModel uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update AGM: [" + uiEntity.getCurie() + "]";

		String curie = validateCurie(uiEntity);
		if (curie == null) {
			throw new ApiErrorException(response);
		}

		AffectedGenomicModel dbEntity = affectedGenomicModelDAO.find(curie);
		if (dbEntity == null) {
			addMessageResponse("curie", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}
		
		dbEntity = (AffectedGenomicModel) validateAuditedObjectFields(uiEntity, dbEntity, false);
		
		return validateAffectedGenomicModel(uiEntity, dbEntity);
	}
	
	public AffectedGenomicModel validateAffectedGenomicModelCreate(AffectedGenomicModel uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create AGM [" + uiEntity.getCurie() + "]";

		AffectedGenomicModel dbEntity = new AffectedGenomicModel();
		String curie = validateCurie(uiEntity);
		dbEntity.setCurie(curie);
		
		dbEntity = (AffectedGenomicModel) validateAuditedObjectFields(uiEntity, dbEntity, true);
		
		return validateAffectedGenomicModel(uiEntity, dbEntity);
	}
	
	private AffectedGenomicModel validateAffectedGenomicModel(AffectedGenomicModel uiEntity, AffectedGenomicModel dbEntity) {

		String name = handleStringField(uiEntity.getName());
		dbEntity.setName(name);

		NCBITaxonTerm taxon = validateTaxon(uiEntity, dbEntity);
		dbEntity.setTaxon(taxon);
		
		DataProvider dataProvider = validateDataProvider(uiEntity, dbEntity);
		dbEntity.setDataProvider(dataProvider);
		
		VocabularyTerm subtype = validateSubtype(uiEntity, dbEntity);
		dbEntity.setSubtype(subtype);

		List<Long> currentXrefIds;
		if (dbEntity.getCrossReferences() == null) {
			currentXrefIds = new ArrayList<>();
		} else {
			currentXrefIds = dbEntity.getCrossReferences().stream().map(CrossReference::getId).collect(Collectors.toList());
		}
		
		List<CrossReference> crossReferences = validateCrossReferences(uiEntity, dbEntity);
		dbEntity.setCrossReferences(crossReferences);
		List<Long> mergedIds = crossReferences == null ? new ArrayList<>() :
			crossReferences.stream().map(CrossReference::getId).collect(Collectors.toList());
		for (Long currentXrefId : currentXrefIds) {
			if (!mergedIds.contains(currentXrefId)) {
				crossReferenceDAO.remove(currentXrefId);
			}
		}

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}
	
	public VocabularyTerm validateSubtype(AffectedGenomicModel uiEntity, AffectedGenomicModel dbEntity) {
		String field = "subtype";
		if (uiEntity.getSubtype() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm subtype = vocabularyTermService.getTermInVocabulary(VocabularyConstants.AGM_SUBTYPE_VOCABULARY, uiEntity.getSubtype().getName()).getEntity();
		if (subtype == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (subtype.getObsolete() && (dbEntity.getSubtype() == null || !subtype.getName().equals(dbEntity.getSubtype().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return subtype;
	}

}
