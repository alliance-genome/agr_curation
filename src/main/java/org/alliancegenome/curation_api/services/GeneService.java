package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseEntityCrudService;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.GeneValidator;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneService extends BaseEntityCrudService<Gene, GeneDAO> {

	@Inject
	GeneDAO geneDAO;
	@Inject
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	CrossReferenceService crossReferenceService;
	@Inject
	SynonymService synonymService;
	@Inject
	SoTermDAO soTermDAO;
	@Inject
	GeneValidator geneValidator;
	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	@Inject 
	NcbiTaxonTermDAO ncbiTaxonTermDAO;
	@Inject
	PersonService personService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> update(Gene uiEntity) {
		Gene dbEntity = geneValidator.validateAnnotation(uiEntity);
		return new ObjectResponse<Gene>(geneDAO.persist(dbEntity));
	}

	@Transactional
	public Gene getByIdOrCurie(String id) {
		Gene gene = geneDAO.getByIdOrCurie(id);
		if (gene != null) {
			gene.getSynonyms().size();
			gene.getSecondaryIdentifiers().size();
		}
		return gene;
	}

	@Transactional
	public Gene upsert(GeneDTO dto) throws ObjectUpdateException {
		Gene gene = validateGeneDTO(dto);
		
		if (gene == null) return null;
		
		return geneDAO.persist(gene);
	}
	
	public void removeNonUpdatedGenes(String taxonIds, List<String> geneCuriesBefore, List<String> geneCuriesAfter) {
		log.debug("runLoad: After: " + taxonIds + " " + geneCuriesAfter.size());

		List<String> distinctAfter = geneCuriesAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + taxonIds + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(geneCuriesBefore, distinctAfter);
		log.debug("runLoad: Remove: " + taxonIds + " " + curiesToRemove.size());

		log.info("Deleting disease annotations linked to " + curiesToRemove.size() + " unloaded genes");
		List<String> foundGeneCuries = new ArrayList<String>();
		for (String curie : curiesToRemove) {
			Gene gene = geneDAO.find(curie);
			if (gene != null) {
				foundGeneCuries.add(curie);
			} else {
				log.error("Failed getting gene: " + curie);
			}
		}
		geneDAO.deleteReferencingDiseaseAnnotations(foundGeneCuries);
		foundGeneCuries.forEach(curie -> {delete(curie);});
		log.info("Deletion of disease annotations linked to unloaded genes finished");
	}
	
	public List<String> getCuriesByTaxonId(String taxonId) {
		List<String> curies = geneDAO.findAllCuriesByTaxon(taxonId);
		curies.removeIf(Objects::isNull);
		return curies;
	}
	
	private Gene validateGeneDTO(GeneDTO dto) throws ObjectValidationException {
		// Check for required fields
		if (dto.getCurie() == null || dto.getTaxon() == null || dto.getSymbol() == null) {
			throw new ObjectValidationException(dto, "Entry for gene " + dto.getCurie() + " missing required fields - skipping");
		}

		Gene gene = geneDAO.find(dto.getCurie());
		if (gene == null) {
			gene = new Gene();
			gene.setCurie(dto.getCurie());
		} 
		
		// Validate taxon ID
		ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.get(dto.getTaxon());
		if (taxonResponse.getEntity() == null) {
			throw new ObjectValidationException(dto, "Invalid taxon ID for gene " + dto.getCurie() + " - skipping");
		}
		gene.setTaxon(taxonResponse.getEntity());
		
		gene.setSymbol(dto.getSymbol());
		
		if (dto.getName() != null) gene.setName(dto.getName());
		
		if (dto.getCreatedBy() != null) {
			Person createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedBy());
			gene.setCreatedBy(createdBy);
		}
		if (dto.getUpdatedBy() != null) {
			Person updatedBy = personService.fetchByUniqueIdOrCreate(dto.getUpdatedBy());
			gene.setUpdatedBy(updatedBy);
		}
		
		gene.setInternal(dto.getInternal());
		gene.setObsolete(dto.getObsolete());

		if (dto.getDateUpdated() != null) {
			OffsetDateTime dateLastModified;
			try {
				dateLastModified = OffsetDateTime.parse(dto.getDateUpdated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_updated in " + gene.getCurie() + " - skipping");
			}
			gene.setDateUpdated(dateLastModified);
		}

		if (dto.getDateCreated() != null) {
			OffsetDateTime creationDate;
			try {
				creationDate = OffsetDateTime.parse(dto.getDateCreated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_created in " + gene.getCurie() + " - skipping");
			}
			gene.setDateCreated(creationDate);
		}
		
		return gene;
	}

}
