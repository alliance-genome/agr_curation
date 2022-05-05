package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.dao.ontology.AnatomicalTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ChemicalTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ExperimentalConditionOntologyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.ExperimentalConditionSummary;
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
    @Inject
    ExperimentalConditionSummary experimentalConditionSummary;
    @Inject
    PersonService personService;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(experimentalConditionDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<ExperimentalCondition> update(ExperimentalCondition uiEntity) {
        ExperimentalCondition dbEntity = experimentalConditionValidator.validateCondition(uiEntity);
        return new ObjectResponse<>(experimentalConditionDAO.persist(dbEntity));
    }
    
    public ExperimentalCondition validateExperimentalConditionDTO(ExperimentalConditionDTO dto) throws ObjectValidationException {
        String uniqueId = DiseaseAnnotationCurie.getExperimentalConditionCurie(dto);

        ExperimentalCondition experimentalCondition;
        SearchResponse<ExperimentalCondition> searchResponse = experimentalConditionDAO.findByField("uniqueId", uniqueId);
        if (searchResponse == null || searchResponse.getSingleResult() == null) {
            experimentalCondition = new ExperimentalCondition();
            experimentalCondition.setUniqueId(uniqueId);
        } else {
            experimentalCondition = searchResponse.getSingleResult();
        }
        
        if (dto.getConditionChemical() != null) {
            ChemicalTerm term = chemicalTermDAO.find(dto.getConditionChemical());
            if (term == null) {
                throw new ObjectValidationException(dto, "Invalid ChemicalOntologyId - skipping annotation");
            }
            experimentalCondition.setConditionChemical(term);
        }
        if (dto.getConditionId() != null) {
            ExperimentalConditionOntologyTerm term = experimentalConditionOntologyTermDAO.find(dto.getConditionId());
            if (term == null) {
                throw new ObjectValidationException(dto, "Invalid ConditionId - skipping annotation");
            }
            experimentalCondition.setConditionId(term);
        }
        if (dto.getConditionClass() != null) {
            ZecoTerm term = zecoTermDAO.find(dto.getConditionClass());
            if (term == null || term.getSubsets().isEmpty() || !term.getSubsets().contains(OntologyConstants.ZECO_AGR_SLIM_SUBSET)) {
                throw new ObjectValidationException(dto, "Invalid ConditionClass - skipping annotation");
            }
            experimentalCondition.setConditionClass(term);
        }
        else {
            throw new ObjectValidationException(dto, "ConditionClassId is a required field - skipping annotation");
        }
        
        if (dto.getConditionAnatomy() != null) {
            AnatomicalTerm term = anatomicalTermDAO.find(dto.getConditionAnatomy());
            if (term == null) {
                throw new ObjectValidationException(dto, "Invalid AnatomicalOntologyId - skipping annotation");
            }
            experimentalCondition.setConditionAnatomy(term);
        }
        if (dto.getConditionTaxon() != null) {
            NCBITaxonTerm term = ncbiTaxonTermDAO.find(dto.getConditionTaxon());
            if (term == null) {
                term = ncbiTaxonTermDAO.downloadAndSave(dto.getConditionTaxon());
            }
            if (term == null) {
                throw new ObjectValidationException(dto, "Invalid NCBITaxonId - skipping annotation");
            }
            experimentalCondition.setConditionTaxon(term);
        }
        if (dto.getConditionGeneOntology() != null) {
            GOTerm term = goTermDAO.find(dto.getConditionGeneOntology());
            if (term == null) {
                throw new ObjectValidationException(dto, "Invalid GeneOntologyId - skipping annotation");
            }
            experimentalCondition.setConditionGeneOntology(term);
        }
        if (dto.getConditionQuantity() != null)
            experimentalCondition.setConditionQuantity(dto.getConditionQuantity());
        if (dto.getConditionFreeText() != null)
            experimentalCondition.setConditionFreeText(dto.getConditionFreeText());
        if (dto.getConditionStatement() == null) {
            throw new ObjectValidationException(dto, "ConditionStatement is a required field - skipping annotation");
        }
        experimentalCondition.setConditionStatement(dto.getConditionStatement());
        
        experimentalCondition.setInternal(dto.getInternal());
        
        if (dto.getCreatedBy() != null) {
            Person createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedBy());
            experimentalCondition.setCreatedBy(createdBy);
        }
        if (dto.getModifiedBy() != null) {
            Person modifiedBy = personService.fetchByUniqueIdOrCreate(dto.getModifiedBy());
            experimentalCondition.setModifiedBy(modifiedBy);
        }
        
        if (dto.getDateUpdated() != null) {
            OffsetDateTime dateLastModified;
            try {
                dateLastModified = OffsetDateTime.parse(dto.getDateUpdated());
            } catch (DateTimeParseException e) {
                throw new ObjectValidationException(dto, "Could not parse date_updated in - skipping");
            }
            experimentalCondition.setDateUpdated(dateLastModified);
        }

        if (dto.getDateCreated() != null) {
            OffsetDateTime creationDate;
            try {
                creationDate = OffsetDateTime.parse(dto.getDateCreated());
            } catch (DateTimeParseException e) {
                throw new ObjectValidationException(dto, "Could not parse date_created in - skipping");
            }
            experimentalCondition.setDateCreated(creationDate);
        }
        
        String conditionSummary = experimentalConditionSummary.getConditionSummary(dto);
        experimentalCondition.setConditionSummary(conditionSummary);
        
        return experimentalConditionDAO.persist(experimentalCondition);
    
    }
    
}
