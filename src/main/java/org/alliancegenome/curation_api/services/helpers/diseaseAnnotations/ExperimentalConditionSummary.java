package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;
import org.alliancegenome.curation_api.services.ontology.AnatomicalTermService;
import org.alliancegenome.curation_api.services.ontology.ChemicalTermService;
import org.alliancegenome.curation_api.services.ontology.ExperimentalConditionOntologyTermService;
import org.alliancegenome.curation_api.services.ontology.GoTermService;
import org.alliancegenome.curation_api.services.ontology.ZecoTermService;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class ExperimentalConditionSummary {

	@Inject
	ZecoTermService zecoTermService;
	@Inject
	ExperimentalConditionOntologyTermService expCondTermService;
	@Inject
	AnatomicalTermService anatomicalTermService;
	@Inject
	GoTermService goTermService;
	@Inject
	NcbiTaxonTermDAO ncbiTaxonTermDAO;
	@Inject
	ChemicalTermService chemicalTermService;

	public static String getConditionSummary(ExperimentalCondition condition) {
		UniqueIdGeneratorHelper conditionSummary = new UniqueIdGeneratorHelper();

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
		UniqueIdGeneratorHelper conditionSummary = new UniqueIdGeneratorHelper();

		if (StringUtils.isNotBlank(conditionDto.getConditionClassCurie())) {
			ZECOTerm conditionClass = zecoTermService.findByCurieOrSecondaryId(conditionDto.getConditionClassCurie());
			conditionSummary.add(conditionClass.getName());
		}

		if (StringUtils.isNotBlank(conditionDto.getConditionIdCurie())) {
			ExperimentalConditionOntologyTerm conditionId = expCondTermService.findByCurieOrSecondaryId(conditionDto.getConditionIdCurie());
			conditionSummary.add(conditionId.getName());
		}

		if (StringUtils.isNotBlank(conditionDto.getConditionAnatomyCurie())) {
			AnatomicalTerm conditionAnatomy = anatomicalTermService.findByCurieOrSecondaryId(conditionDto.getConditionAnatomyCurie());
			conditionSummary.add(conditionAnatomy.getName());
		}

		if (StringUtils.isNotBlank(conditionDto.getConditionGeneOntologyCurie())) {
			GOTerm conditionGeneOntology = goTermService.findByCurieOrSecondaryId(conditionDto.getConditionGeneOntologyCurie());
			conditionSummary.add(conditionGeneOntology.getName());
		}

		if (StringUtils.isNotBlank(conditionDto.getConditionChemicalCurie())) {
			ChemicalTerm conditionChemical = chemicalTermService.findByCurieOrSecondaryId(conditionDto.getConditionChemicalCurie());
			conditionSummary.add(conditionChemical.getName());
		}

		if (StringUtils.isNotBlank(conditionDto.getConditionTaxonCurie())) {
			NCBITaxonTerm conditionTaxon = ncbiTaxonTermDAO.find(conditionDto.getConditionTaxonCurie());
			conditionSummary.add(conditionTaxon.getName());
		}

		if (StringUtils.isNotBlank(conditionDto.getConditionQuantity()))
			conditionSummary.add(conditionDto.getConditionQuantity());

		if (StringUtils.isNotBlank(conditionDto.getConditionFreeText()))
			conditionSummary.add(conditionDto.getConditionFreeText());

		return conditionSummary.getSummary();
	}
}
