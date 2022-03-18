package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ontology.AnatomicalTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ChemicalTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ExperimentalConditionOntologyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.alliancegenome.curation_api.model.ingest.dto.*;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.services.helpers.CurieGeneratorHelper;

public abstract class ExperimentalConditionSummary {
    
    @Inject ZecoTermDAO zecoTermDAO;
    @Inject ExperimentalConditionOntologyTermDAO expCondTermDAO;
    @Inject AnatomicalTermDAO anatomicalTermDAO;
    @Inject GoTermDAO goTermDAO;
    @Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;
    @Inject ChemicalTermDAO chemicalTermDAO;

    public static String getConditionSummary(ExperimentalCondition condition) {
        CurieGeneratorHelper conditionSummary = new CurieGeneratorHelper();
    
        conditionSummary.add(condition.getConditionClass().getName());
        
        if (condition.getConditionId() != null)
            conditionSummary.add(condition.getConditionId().getName());
        
        if (condition.getConditionAnatomy() != null)
            conditionSummary.add(condition.getConditionAnatomy().getName());
        
        if (condition.getConditionGeneOntology() != null)
            conditionSummary.add(condition.getConditionGeneOntology().getName());
        
        if (condition.getConditionTaxon() != null)
            conditionSummary.add(condition.getConditionTaxon().getName());
        
        if (condition.getConditionChemical() != null)
            conditionSummary.add(condition.getConditionChemical().getName());
        
        if (condition.getConditionQuantity() != null)
            conditionSummary.add(condition.getConditionQuantity());
        
        if (condition.getConditionFreeText() != null)
            conditionSummary.add(condition.getConditionFreeText());
        
        
        return conditionSummary.getCurie();
    }
    
    public String getConditionSummary(ExperimentalConditionDTO conditionDto) {
        CurieGeneratorHelper conditionSummary = new CurieGeneratorHelper();
        
        ZecoTerm conditionClass = zecoTermDAO.find(conditionDto.getConditionClass());
        conditionSummary.add(conditionClass.getName());
        
        if (conditionDto.getConditionId() != null) {
            ExperimentalConditionOntologyTerm conditionId = expCondTermDAO.find(conditionDto.getConditionId());
            conditionSummary.add(conditionId.getName());
        }
        
        if (conditionDto.getConditionAnatomy() != null) {
            AnatomicalTerm conditionAnatomy = anatomicalTermDAO.find(conditionDto.getConditionAnatomy());
            conditionSummary.add(conditionAnatomy.getName());
        }
        
        if (conditionDto.getConditionGeneOntology() != null) {
            GOTerm conditionGeneOntology = goTermDAO.find(conditionDto.getConditionGeneOntology());
            conditionSummary.add(conditionGeneOntology.getName());
        }
        
        if (conditionDto.getConditionTaxon() != null) {
            NCBITaxonTerm conditionTaxon = ncbiTaxonTermDAO.find(conditionDto.getConditionTaxon());
            conditionSummary.add(conditionTaxon.getName());
        }
        
        if (conditionDto.getConditionChemical() != null) {
            ChemicalTerm conditionChemical = chemicalTermDAO.find(conditionDto.getConditionChemical());
            conditionSummary.add(conditionChemical.getName());
        }

        if (conditionDto.getConditionQuantity() != null)
            conditionSummary.add(conditionDto.getConditionQuantity());
        
        if (conditionDto.getConditionFreeText() != null)
            conditionSummary.add(conditionDto.getConditionFreeText());
        
        return conditionSummary.getCurie();
    }
    
    public String getConditionSummary(ExperimentalConditionFmsDTO conditionFmsDto) {
        CurieGeneratorHelper conditionSummary = new CurieGeneratorHelper();
        
        ZecoTerm conditionClass = zecoTermDAO.find(conditionFmsDto.getConditionClassId());
        conditionSummary.add(conditionClass.getName());
        
        if (conditionFmsDto.getConditionId() != null) {
            ExperimentalConditionOntologyTerm conditionId = expCondTermDAO.find(conditionFmsDto.getConditionId());
            conditionSummary.add(conditionId.getName());
        }
        
        if (conditionFmsDto.getAnatomicalOntologyId() != null) {
            AnatomicalTerm conditionAnatomy = anatomicalTermDAO.find(conditionFmsDto.getAnatomicalOntologyId());
            conditionSummary.add(conditionAnatomy.getName());
        }
        
        if (conditionFmsDto.getGeneOntologyId() != null) {
            GOTerm conditionGeneOntology = goTermDAO.find(conditionFmsDto.getGeneOntologyId());
            conditionSummary.add(conditionGeneOntology.getName());
        }
        
        if (conditionFmsDto.getNcbiTaxonId() != null) {
            NCBITaxonTerm conditionTaxon = ncbiTaxonTermDAO.find(conditionFmsDto.getNcbiTaxonId());
            conditionSummary.add(conditionTaxon.getName());
        }
        
        if (conditionFmsDto.getChemicalOntologyId() != null) {
            ChemicalTerm conditionChemical = chemicalTermDAO.find(conditionFmsDto.getChemicalOntologyId());
            conditionSummary.add(conditionChemical.getName());
        }
        
        if (conditionFmsDto.getConditionQuantity() != null)
            conditionSummary.add(conditionFmsDto.getConditionQuantity());
        
        return conditionSummary.getCurie();
    }
}
