package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
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
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.validators.ExperimentalConditionValidator;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class ExperimentalConditionService extends BaseCrudService<ExperimentalCondition, ExperimentalConditionDAO> {

    @Inject
    ExperimentalConditionDAO experimentalConditionDAO;
    @Inject
    ExperimentalConditionValidator experimentalConditionValidator;
    @Inject
    ZecoTermDAO zecoTermDAO;
    @Inject
    ChemicalTermDAO chemicalTermDAO;
    @Inject
    AnatomicalTermDAO anatomicalTermDAO;
    @Inject
    NcbiTaxonTermDAO ncbiTaxonTermDAO;
    @Inject
    GoTermDAO goTermDAO;
    @Inject
    ExperimentalConditionOntologyTermDAO experimentalConditionOntologyTermDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(experimentalConditionDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<ExperimentalCondition> update(ExperimentalCondition uiEntity) {
        ExperimentalCondition dbEntity = experimentalConditionValidator.validateCondition(uiEntity);
        return new ObjectResponse<ExperimentalCondition>(experimentalConditionDAO.persist(dbEntity));
    }
    
    public ExperimentalCondition validateExperimentalConditionDTO(ExperimentalConditionDTO dto) {
        ExperimentalCondition experimentalCondition = new ExperimentalCondition();
        
        if (dto.getConditionChemical() != null) {
            ChemicalTerm term = chemicalTermDAO.find(dto.getConditionChemical());
            if (term == null) {
                log("Invalid ChemicalOntologyId - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionChemical(term);
        }
        if (dto.getConditionId() != null) {
            ExperimentalConditionOntologyTerm term = experimentalConditionOntologyTermDAO.find(dto.getConditionId());
            if (term == null) {
                log("Invalid ConditionId - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionId(term);
        }
        if (dto.getConditionClass() != null) {
            ZecoTerm term = zecoTermDAO.find(dto.getConditionClass());
            if (term == null) {
                log("Invalid ConditionClass - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionClass(term);
        }
        else {
            log("ConditionClassId is a required field - skipping annotation");
            return null;
        }
        
        if (dto.getConditionAnatomy() != null) {
            AnatomicalTerm term = anatomicalTermDAO.find(dto.getConditionAnatomy());
            if (term == null) {
                log("Invalid AnatomicalOntologyId - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionAnatomy(term);
        }
        if (dto.getConditionTaxon() != null) {
            NCBITaxonTerm term = ncbiTaxonTermDAO.find(dto.getConditionTaxon());
            if (term == null) {
                term = ncbiTaxonTermDAO.downloadAndSave(dto.getConditionTaxon());
            }
            if (term == null) {
                log("Invalid NCBITaxonId - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionTaxon(term);
        }
        if (dto.getConditionGeneOntology() != null) {
            GOTerm term = goTermDAO.find(dto.getConditionGeneOntology());
            if (term == null) {
                log("Invalid GeneOntologyId - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionGeneOntology(term);
        }
        if (dto.getConditionQuantity() != null)
            experimentalCondition.setConditionQuantity(dto.getConditionQuantity());
        if (dto.getConditionStatement() == null) {
            log("ConditionStatement is a required field - skipping annotation");
            return null;
        }
        experimentalCondition.setConditionStatement(dto.getConditionStatement());
        
        experimentalCondition.setUniqueId(DiseaseAnnotationCurie.getExperimentalConditionCurie(dto));
        
        return experimentalCondition;
    }
    
    private void log(String message) {
        log.debug(message);
        // log.info(message);
    }
    
}
