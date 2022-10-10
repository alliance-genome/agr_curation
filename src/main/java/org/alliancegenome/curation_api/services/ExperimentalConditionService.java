package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.dao.ontology.AnatomicalTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ChemicalTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ExperimentalConditionOntologyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.ExperimentalConditionSummary;
import org.alliancegenome.curation_api.services.helpers.validators.ExperimentalConditionValidator;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class ExperimentalConditionService extends BaseEntityCrudService<ExperimentalCondition, ExperimentalConditionDAO> {

	@Inject ExperimentalConditionDAO experimentalConditionDAO;
	@Inject ExperimentalConditionValidator experimentalConditionValidator;
	@Inject ZecoTermDAO zecoTermDAO;
	@Inject ChemicalTermDAO chemicalTermDAO;
	@Inject AnatomicalTermDAO anatomicalTermDAO;
	@Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;
	@Inject GoTermDAO goTermDAO;
	@Inject ExperimentalConditionOntologyTermDAO experimentalConditionOntologyTermDAO;
	@Inject ExperimentalConditionSummary experimentalConditionSummary;
	@Inject PersonService personService;
	@Inject AuditedObjectService<ExperimentalCondition, ExperimentalConditionDTO> auditedObjectService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(experimentalConditionDAO);
	}
	
	@Override
	@Transactional
	public ObjectResponse<ExperimentalCondition> update(ExperimentalCondition uiEntity) {
		ExperimentalCondition dbEntity = experimentalConditionValidator.validateExperimentalConditionUpdate(uiEntity);
		return new ObjectResponse<>(experimentalConditionDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<ExperimentalCondition> create(ExperimentalCondition uiEntity) {
		ExperimentalCondition dbEntity = experimentalConditionValidator.validateExperimentalConditionCreate(uiEntity);
		return new ObjectResponse<>(experimentalConditionDAO.persist(dbEntity));
	}
	
	public ObjectResponse<ExperimentalCondition> validateExperimentalConditionDTO(ExperimentalConditionDTO dto) {
		ObjectResponse<ExperimentalCondition> ecResponse = new ObjectResponse<ExperimentalCondition>();
		
		String uniqueId = DiseaseAnnotationCurie.getExperimentalConditionCurie(dto);

		ExperimentalCondition experimentalCondition;
		SearchResponse<ExperimentalCondition> searchResponse = experimentalConditionDAO.findByField("uniqueId", uniqueId);
		if (searchResponse == null || searchResponse.getSingleResult() == null) {
			experimentalCondition = new ExperimentalCondition();
			experimentalCondition.setUniqueId(uniqueId);
		} else {
			experimentalCondition = searchResponse.getSingleResult();
		}
		
		ObjectResponse<ExperimentalCondition> aoResponse = auditedObjectService.validateAuditedObjectDTO(experimentalCondition, dto);
    	experimentalCondition = aoResponse.getEntity();
    	ecResponse.addErrorMessages(aoResponse.getErrorMessages());
		
		ChemicalTerm conditionChemical = null;
		if (StringUtils.isNotBlank(dto.getConditionChemical())) {
			conditionChemical = chemicalTermDAO.find(dto.getConditionChemical());
			if (conditionChemical == null)
				ecResponse.addErrorMessage("conditionChemical", ValidationConstants.INVALID_MESSAGE);
		}
		experimentalCondition.setConditionChemical(conditionChemical);
		
		ExperimentalConditionOntologyTerm conditionId = null;
		if (StringUtils.isNotBlank(dto.getConditionId())) {
			conditionId = experimentalConditionOntologyTermDAO.find(dto.getConditionId());
			if (conditionId == null)
				ecResponse.addErrorMessage("conditionId", ValidationConstants.INVALID_MESSAGE);
		}
		experimentalCondition.setConditionId(conditionId);

		if (StringUtils.isNotBlank(dto.getConditionClass())) {
			ZECOTerm term = zecoTermDAO.find(dto.getConditionClass());
			if (term == null || term.getSubsets().isEmpty() || !term.getSubsets().contains(OntologyConstants.ZECO_AGR_SLIM_SUBSET))
				ecResponse.addErrorMessage("conditionClass", ValidationConstants.INVALID_MESSAGE);
			experimentalCondition.setConditionClass(term);
		}
		else {
			ecResponse.addErrorMessage("conditionClass", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		AnatomicalTerm conditionAnatomy = null;
		if (StringUtils.isNotBlank(dto.getConditionAnatomy())) {
			conditionAnatomy = anatomicalTermDAO.find(dto.getConditionAnatomy());
			if (conditionAnatomy == null)
				ecResponse.addErrorMessage("conditionAnatomy", ValidationConstants.REQUIRED_MESSAGE);
		}
		experimentalCondition.setConditionAnatomy(conditionAnatomy);
		
		NCBITaxonTerm conditionTaxon = null;
		if (StringUtils.isNotBlank(dto.getConditionTaxon())) {
			conditionTaxon = ncbiTaxonTermDAO.find(dto.getConditionTaxon());
			if (conditionTaxon == null) {
				conditionTaxon = ncbiTaxonTermDAO.downloadAndSave(dto.getConditionTaxon());
			}
			if (conditionTaxon == null)
				ecResponse.addErrorMessage("conditionTaxon", ValidationConstants.INVALID_MESSAGE);
		}
		experimentalCondition.setConditionTaxon(conditionTaxon);

		GOTerm conditionGeneOntology = null;
		if (StringUtils.isNotBlank(dto.getConditionGeneOntology())) {
			conditionGeneOntology = goTermDAO.find(dto.getConditionGeneOntology());
			if (conditionGeneOntology == null)
				ecResponse.addErrorMessage("conditionGeneOntology", ValidationConstants.INVALID_MESSAGE);
		}
		experimentalCondition.setConditionGeneOntology(conditionGeneOntology);
		
		String conditionQuantity = null;
		if (StringUtils.isNotBlank(dto.getConditionQuantity()))
			conditionQuantity = dto.getConditionQuantity();
		experimentalCondition.setConditionQuantity(conditionQuantity);
		
		String conditionFreeText = null;
		if (StringUtils.isNotBlank(dto.getConditionFreeText()))
			conditionFreeText = dto.getConditionFreeText();
		experimentalCondition.setConditionFreeText(conditionFreeText);
				
		if (!ecResponse.hasErrors()) {
			String conditionSummary = experimentalConditionSummary.getConditionSummary(dto);
			experimentalCondition.setConditionSummary(conditionSummary);
		}
		
		ecResponse.setEntity(experimentalCondition);
		
		return ecResponse;
	}
	
}
