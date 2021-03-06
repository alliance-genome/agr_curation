package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.dao.ontology.AnatomicalTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ChemicalTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ExperimentalConditionOntologyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.ExperimentalConditionSummary;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class ExperimentalConditionValidator extends AuditedObjectValidator<ExperimentalCondition> {

	@Inject
	ExperimentalConditionDAO experimentalConditionDAO;
	@Inject
	GoTermDAO goTermDAO;
	@Inject
	ZecoTermDAO zecoTermDAO;
	@Inject
	AnatomicalTermDAO anatomicalTermDAO;
	@Inject
	ChemicalTermDAO chemicalTermDAO;
	@Inject
	ExperimentalConditionOntologyTermDAO ecOntologyTermDAO;
	@Inject
	NcbiTaxonTermDAO ncbiTaxonTermDAO;
	
	
	public ExperimentalCondition validateCondition(ExperimentalCondition uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not update ExperimentalCondition: [" + uiEntity.getUniqueId() + "]";
		
		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Experimental Condition ID provided");
			throw new ApiErrorException(response);
		}
		ExperimentalCondition dbEntity = experimentalConditionDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find ExperimentalCondition with ID: [" + id + "]");
			throw new ApiErrorException(response);
			// do not continue validation for update if Disease Annotation ID has not been found
		}
		
		dbEntity = (ExperimentalCondition) validateAuditedObjectFields(uiEntity, dbEntity);
		
		ZecoTerm conditionClass = validateConditionClass(uiEntity, dbEntity);
		dbEntity.setConditionClass(conditionClass);
		
		ExperimentalConditionOntologyTerm ecOntologyTerm = validateConditionId(uiEntity, dbEntity);
		dbEntity.setConditionId(ecOntologyTerm);
		
		GOTerm conditionGeneOntology = validateConditionGeneOntology(uiEntity, dbEntity);
		dbEntity.setConditionGeneOntology(conditionGeneOntology);
		
		AnatomicalTerm conditionAnatomy = validateConditionAnatomy(uiEntity, dbEntity);
		dbEntity.setConditionAnatomy(conditionAnatomy);
		
		ChemicalTerm conditionChemical = validateConditionChemical(uiEntity, dbEntity);
		dbEntity.setConditionChemical(conditionChemical);
		
		NCBITaxonTerm conditionTaxon = validateConditionTaxon(uiEntity, dbEntity);
		dbEntity.setConditionTaxon(conditionTaxon);
		
		String conditionStatement = validateConditionStatement(uiEntity, dbEntity);
		dbEntity.setConditionStatement(conditionStatement);
		
		dbEntity.setConditionQuantity(handleStringField(uiEntity.getConditionQuantity()));
		dbEntity.setConditionFreeText(handleStringField(uiEntity.getConditionFreeText()));
			
		dbEntity.setConditionSummary(ExperimentalConditionSummary.getConditionSummary(dbEntity));
		
		String uniqueId = DiseaseAnnotationCurie.getExperimentalConditionCurie(dbEntity);
		if (!uniqueId.equals(uiEntity.getUniqueId())) {
			SearchResponse<ExperimentalCondition> dbSearchResponse = experimentalConditionDAO.findByField("uniqueId", uniqueId);
			if (dbSearchResponse != null) {
				addMessageResponse("uniqueId", ValidationConstants.NON_UNIQUE_MESSAGE);
			} else {
				dbEntity.setUniqueId(uniqueId);
			}
		}
		
		if (response.hasErrors()) {
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}
		
		return dbEntity;
	}
	
	public ZecoTerm validateConditionClass(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionClass";
		if (ObjectUtils.isEmpty(uiEntity.getConditionClass()) || StringUtils.isBlank(uiEntity.getConditionClass().getCurie())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		ZecoTerm zecoTerm = zecoTermDAO.find(uiEntity.getConditionClass().getCurie());
		if (zecoTerm == null || zecoTerm.getSubsets().isEmpty() || !zecoTerm.getSubsets().contains(OntologyConstants.ZECO_AGR_SLIM_SUBSET)) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		else if (zecoTerm.getObsolete() && !zecoTerm.getCurie().equals(dbEntity.getConditionClass().getCurie())) {
			addMessageResponse(ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return zecoTerm;
	}
	
	public ExperimentalConditionOntologyTerm validateConditionId(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionId";
		if (ObjectUtils.isEmpty(uiEntity.getConditionId()) || StringUtils.isBlank(uiEntity.getConditionId().getCurie())) {
			return null;
		}
		ExperimentalConditionOntologyTerm ecOntologyTerm = ecOntologyTermDAO.find(uiEntity.getConditionId().getCurie());
		if (ecOntologyTerm == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		else if (ecOntologyTerm.getObsolete() && !ecOntologyTerm.getCurie().equals(dbEntity.getConditionId().getCurie())) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return ecOntologyTerm;
	}
	
	public GOTerm validateConditionGeneOntology(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionGeneOntology";
		if (ObjectUtils.isEmpty(uiEntity.getConditionGeneOntology()) || StringUtils.isBlank(uiEntity.getConditionGeneOntology().getCurie())) {
			return null;
		}
		GOTerm goTerm = goTermDAO.find(uiEntity.getConditionGeneOntology().getCurie());
		if (goTerm == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		else if (goTerm.getObsolete() && !goTerm.getCurie().equals(dbEntity.getConditionGeneOntology().getCurie())) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return goTerm;
	}
	
	public AnatomicalTerm validateConditionAnatomy(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionAnatomy";
		if (ObjectUtils.isEmpty(uiEntity.getConditionAnatomy()) || StringUtils.isBlank(uiEntity.getConditionAnatomy().getCurie())) {
			return null;
		}
		AnatomicalTerm anatomicalTerm = anatomicalTermDAO.find(uiEntity.getConditionAnatomy().getCurie());
		if (anatomicalTerm == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		else if (anatomicalTerm.getObsolete() && !anatomicalTerm.getCurie().equals(dbEntity.getConditionAnatomy().getCurie())) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return anatomicalTerm;
	}
	
	public ChemicalTerm validateConditionChemical(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionChemical";
		if (ObjectUtils.isEmpty(uiEntity.getConditionChemical()) || StringUtils.isBlank(uiEntity.getConditionChemical().getCurie())) {
			return null;
		}
		ChemicalTerm chemicalTerm = chemicalTermDAO.find(uiEntity.getConditionChemical().getCurie());
		if (chemicalTerm == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		else if (chemicalTerm.getObsolete() != null && chemicalTerm.getObsolete() && !chemicalTerm.getCurie().equals(dbEntity.getConditionChemical().getCurie())) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return chemicalTerm;
	}

	public NCBITaxonTerm validateConditionTaxon(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionTaxon";
		if (ObjectUtils.isEmpty(uiEntity.getConditionTaxon()) || StringUtils.isBlank(uiEntity.getConditionTaxon().getCurie())) {
			return null;
		}
		NCBITaxonTerm taxonTerm = ncbiTaxonTermDAO.find(uiEntity.getConditionTaxon().getCurie());
		if (taxonTerm == null) {
			taxonTerm = ncbiTaxonTermDAO.downloadAndSave(uiEntity.getConditionTaxon().getCurie());
		}
		if (taxonTerm == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		else if (taxonTerm.getObsolete() && !taxonTerm.getCurie().equals(dbEntity.getConditionTaxon().getCurie())) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return taxonTerm;
	}
	
	public String validateConditionStatement(ExperimentalCondition uiEntity, ExperimentalCondition dbEntity) {
		String field = "conditionStatement";
		String conditionStatement = uiEntity.getConditionStatement();
		if (StringUtils.isBlank(conditionStatement)) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		return conditionStatement;
	}
}
