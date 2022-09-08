package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

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
		ExperimentalCondition dbEntity = experimentalConditionValidator.validateExperimentalConditionUpdate(uiEntity);
		return new ObjectResponse<>(experimentalConditionDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<ExperimentalCondition> create(ExperimentalCondition uiEntity) {
		ExperimentalCondition dbEntity = experimentalConditionValidator.validateExperimentalConditionCreate(uiEntity);
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
		
		ChemicalTerm conditionChemical = null;
		if (StringUtils.isNotBlank(dto.getConditionChemical())) {
			conditionChemical = chemicalTermDAO.find(dto.getConditionChemical());
			if (conditionChemical == null) {
				throw new ObjectValidationException(dto, "Invalid ChemicalOntologyId - skipping annotation");
			}
			
		}
		experimentalCondition.setConditionChemical(conditionChemical);
		
		ExperimentalConditionOntologyTerm conditionId = null;
		if (StringUtils.isNotBlank(dto.getConditionId())) {
			conditionId = experimentalConditionOntologyTermDAO.find(dto.getConditionId());
			if (conditionId == null) {
				throw new ObjectValidationException(dto, "Invalid ConditionId - skipping annotation");
			}
		}
		experimentalCondition.setConditionId(conditionId);

		if (StringUtils.isNotBlank(dto.getConditionClass())) {
			ZECOTerm term = zecoTermDAO.find(dto.getConditionClass());
			if (term == null || term.getSubsets().isEmpty() || !term.getSubsets().contains(OntologyConstants.ZECO_AGR_SLIM_SUBSET)) {
				throw new ObjectValidationException(dto, "Invalid ConditionClass - skipping annotation");
			}
			experimentalCondition.setConditionClass(term);
		}
		else {
			throw new ObjectValidationException(dto, "ConditionClassId is a required field - skipping annotation");
		}
		
		AnatomicalTerm conditionAnatomy = null;
		if (StringUtils.isNotBlank(dto.getConditionAnatomy())) {
			conditionAnatomy = anatomicalTermDAO.find(dto.getConditionAnatomy());
			if (conditionAnatomy == null) {
				throw new ObjectValidationException(dto, "Invalid AnatomicalOntologyId - skipping annotation");
			}
		}
		experimentalCondition.setConditionAnatomy(conditionAnatomy);
		
		NCBITaxonTerm conditionTaxon = null;
		if (StringUtils.isNotBlank(dto.getConditionTaxon())) {
			conditionTaxon = ncbiTaxonTermDAO.find(dto.getConditionTaxon());
			if (conditionTaxon == null) {
				conditionTaxon = ncbiTaxonTermDAO.downloadAndSave(dto.getConditionTaxon());
			}
			if (conditionTaxon == null) {
				throw new ObjectValidationException(dto, "Invalid NCBITaxonId - skipping annotation");
			}
		}
		experimentalCondition.setConditionTaxon(conditionTaxon);

		GOTerm conditionGeneOntology = null;
		if (StringUtils.isNotBlank(dto.getConditionGeneOntology())) {
			conditionGeneOntology = goTermDAO.find(dto.getConditionGeneOntology());
			if (conditionGeneOntology == null) {
				throw new ObjectValidationException(dto, "Invalid GeneOntologyId - skipping annotation");
			}
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
		
		if (StringUtils.isBlank(dto.getConditionStatement())) {
			throw new ObjectValidationException(dto, "ConditionStatement is a required field - skipping annotation");
		}
		experimentalCondition.setConditionStatement(dto.getConditionStatement());
		
		experimentalCondition.setInternal(dto.getInternal());
		
		Boolean obsolete = false;
		if (experimentalCondition.getObsolete() != null)
			obsolete = dto.getObsolete();
		experimentalCondition.setObsolete(obsolete);
		
		if (StringUtils.isNotBlank(dto.getCreatedBy())) {
			Person createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedBy());
			experimentalCondition.setCreatedBy(createdBy);
		}
		if (StringUtils.isNotBlank(dto.getUpdatedBy())) {
			Person updatedBy = personService.fetchByUniqueIdOrCreate(dto.getUpdatedBy());
			experimentalCondition.setUpdatedBy(updatedBy);
		}
		
		if (StringUtils.isNotBlank(dto.getDateUpdated())) {
			OffsetDateTime dateLastModified;
			try {
				dateLastModified = OffsetDateTime.parse(dto.getDateUpdated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_updated in - skipping");
			}
			experimentalCondition.setDateUpdated(dateLastModified);
		}

		if (StringUtils.isNotBlank(dto.getDateCreated())) {
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
