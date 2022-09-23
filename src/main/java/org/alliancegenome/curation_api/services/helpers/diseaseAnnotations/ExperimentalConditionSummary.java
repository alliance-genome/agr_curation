package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.*;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class ExperimentalConditionSummary {
	
	@Inject ZecoTermDAO zecoTermDAO;
	@Inject ExperimentalConditionOntologyTermDAO expCondTermDAO;
	@Inject AnatomicalTermDAO anatomicalTermDAO;
	@Inject GoTermDAO goTermDAO;
	@Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;
	@Inject ChemicalTermDAO chemicalTermDAO;

	public static String getConditionSummary(ExperimentalCondition condition) {
		CurieGeneratorHelper conditionSummary = new CurieGeneratorHelper();
	
		if (condition.getConditionClass() != null)
			conditionSummary.add(condition.getConditionClass().getName());
		
		if (condition.getConditionId() != null)
			conditionSummary.add(condition.getConditionId().getName());
		
		if (condition.getConditionAnatomy() != null)
			conditionSummary.add(condition.getConditionAnatomy().getName());
		
		if (condition.getConditionGeneOntology() != null)
			conditionSummary.add(condition.getConditionGeneOntology().getName());
		
		if (condition.getConditionChemical() != null)
			conditionSummary.add(condition.getConditionChemical().getName());
		
		if (condition.getConditionTaxon() != null)
			conditionSummary.add(condition.getConditionTaxon().getName());
		
		if (condition.getConditionQuantity() != null)
			conditionSummary.add(condition.getConditionQuantity());
		
		if (StringUtils.isNotBlank(condition.getConditionFreeText()))
			conditionSummary.add(condition.getConditionFreeText());
		
		
		return conditionSummary.getSummary();
	}
	
	public String getConditionSummary(ExperimentalConditionDTO conditionDto) {
		CurieGeneratorHelper conditionSummary = new CurieGeneratorHelper();
		
		if (StringUtils.isNotBlank(conditionDto.getConditionClass())) {
			ZECOTerm conditionClass = zecoTermDAO.find(conditionDto.getConditionClass());
			conditionSummary.add(conditionClass.getName());
		}
		
		if (StringUtils.isNotBlank(conditionDto.getConditionId())) {
			ExperimentalConditionOntologyTerm conditionId = expCondTermDAO.find(conditionDto.getConditionId());
			conditionSummary.add(conditionId.getName());
		}
		
		if (StringUtils.isNotBlank(conditionDto.getConditionAnatomy())) {
			AnatomicalTerm conditionAnatomy = anatomicalTermDAO.find(conditionDto.getConditionAnatomy());
			conditionSummary.add(conditionAnatomy.getName());
		}
		
		if (StringUtils.isNotBlank(conditionDto.getConditionGeneOntology())) {
			GOTerm conditionGeneOntology = goTermDAO.find(conditionDto.getConditionGeneOntology());
			conditionSummary.add(conditionGeneOntology.getName());
		}

		if (StringUtils.isNotBlank(conditionDto.getConditionChemical())) {
			ChemicalTerm conditionChemical = chemicalTermDAO.find(conditionDto.getConditionChemical());
			conditionSummary.add(conditionChemical.getName());
		}
		
		if (StringUtils.isNotBlank(conditionDto.getConditionTaxon())) {
			NCBITaxonTerm conditionTaxon = ncbiTaxonTermDAO.find(conditionDto.getConditionTaxon());
			conditionSummary.add(conditionTaxon.getName());
		}

		if (StringUtils.isNotBlank(conditionDto.getConditionQuantity()))
			conditionSummary.add(conditionDto.getConditionQuantity());
		
		if (StringUtils.isNotBlank(conditionDto.getConditionFreeText()))
			conditionSummary.add(conditionDto.getConditionFreeText());
		
		return conditionSummary.getSummary();
	}
}
