package org.alliancegenome.curation_api.services.validation.dto.fms;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ExperimentalConditionFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.services.ontology.AnatomicalTermService;
import org.alliancegenome.curation_api.services.ontology.ChemicalTermService;
import org.alliancegenome.curation_api.services.ontology.ExperimentalConditionOntologyTermService;
import org.alliancegenome.curation_api.services.ontology.GoTermService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.ontology.ZecoTermService;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped

public class ExperimentalConditionFmsDTOValidator {
	
	@Inject
	ExperimentalConditionDAO experimentalConditionDAO;
	@Inject
	ZecoTermService zecoTermService;
	@Inject
	ChemicalTermService chemicalTermService;
	@Inject
	AnatomicalTermService anatomicalTermService;
	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	@Inject
	GoTermService goTermService;
	@Inject
	ExperimentalConditionOntologyTermService experimentalConditionOntologyTermService;
	
	public ObjectResponse<ExperimentalCondition> validateExperimentalConditionFmsDTO (ExperimentalConditionFmsDTO dto) {
		ObjectResponse<ExperimentalCondition> ecResponse = new ObjectResponse<>();
		
		String uniqueId = AnnotationUniqueIdHelper.getExperimentalConditionUniqueId(dto);

		ExperimentalCondition experimentalCondition;
		SearchResponse<ExperimentalCondition> searchResponse = experimentalConditionDAO.findByField("uniqueId", uniqueId);
		if (searchResponse == null || searchResponse.getSingleResult() == null) {
			experimentalCondition = new ExperimentalCondition();
			experimentalCondition.setUniqueId(uniqueId);
		} else {
			experimentalCondition = searchResponse.getSingleResult();
		}

		ChemicalTerm conditionChemical = null;
		if (StringUtils.isNotBlank(dto.getChemicalOntologyId())) {
			conditionChemical = chemicalTermService.findByCurieOrSecondaryId(dto.getChemicalOntologyId());
			if (conditionChemical == null)
				ecResponse.addErrorMessage("chemicalOntologyId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getChemicalOntologyId() + ")");
		}
		experimentalCondition.setConditionChemical(conditionChemical);

		ExperimentalConditionOntologyTerm conditionId = null;
		if (StringUtils.isNotBlank(dto.getConditionId())) {
			conditionId = experimentalConditionOntologyTermService.findByCurieOrSecondaryId(dto.getConditionId());
			if (conditionId == null)
				ecResponse.addErrorMessage("conditionId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getConditionId() + ")");
		}
		experimentalCondition.setConditionId(conditionId);

		if (StringUtils.isNotBlank(dto.getConditionClassId())) {
			ZECOTerm term = zecoTermService.findByCurieOrSecondaryId(dto.getConditionClassId());
			if (term == null || term.getSubsets().isEmpty() || !term.getSubsets().contains(OntologyConstants.ZECO_AGR_SLIM_SUBSET))
				ecResponse.addErrorMessage("conditionClassId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getConditionClassId() + ")");
			experimentalCondition.setConditionClass(term);
		} else {
			ecResponse.addErrorMessage("conditionClassId", ValidationConstants.REQUIRED_MESSAGE);
		}

		AnatomicalTerm conditionAnatomy = null;
		if (StringUtils.isNotBlank(dto.getAnatomicalOntologyId())) {
			conditionAnatomy = anatomicalTermService.findByCurieOrSecondaryId(dto.getAnatomicalOntologyId());
			if (conditionAnatomy == null)
				ecResponse.addErrorMessage("anatomicalOntologyId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAnatomicalOntologyId() + ")");
		}
		experimentalCondition.setConditionAnatomy(conditionAnatomy);

		NCBITaxonTerm conditionTaxon = null;
		if (StringUtils.isNotBlank(dto.getNcbiTaxonId())) {
			conditionTaxon = ncbiTaxonTermService.getTaxonFromDB(dto.getNcbiTaxonId());
			if (conditionTaxon == null)
				ecResponse.addErrorMessage("NCBITaxonId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getNcbiTaxonId() + ")");
		}
		experimentalCondition.setConditionTaxon(conditionTaxon);

		GOTerm conditionGeneOntology = null;
		if (StringUtils.isNotBlank(dto.getGeneOntologyId())) {
			conditionGeneOntology = goTermService.findByCurieOrSecondaryId(dto.getGeneOntologyId());
			if (conditionGeneOntology == null)
				ecResponse.addErrorMessage("geneOntologyId", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneOntologyId() + ")");
		}
		experimentalCondition.setConditionGeneOntology(conditionGeneOntology);

		String conditionQuantity = null;
		if (StringUtils.isNotBlank(dto.getConditionQuantity()))
			conditionQuantity = dto.getConditionQuantity();
		experimentalCondition.setConditionQuantity(conditionQuantity);

		if (StringUtils.isNotBlank(dto.getConditionStatement())) {
			experimentalCondition.setConditionSummary(dto.getConditionStatement());
		} else {
			ecResponse.addErrorMessage("conditionStatement", ValidationConstants.REQUIRED_MESSAGE);
		}

		ecResponse.setEntity(experimentalCondition);
		
		return ecResponse;
	}
}
