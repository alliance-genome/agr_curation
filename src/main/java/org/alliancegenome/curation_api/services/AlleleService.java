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

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.helpers.validators.AlleleValidator;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleService extends BaseDTOCrudService<Allele, AlleleDTO, AlleleDAO> {

	@Inject AlleleDAO alleleDAO;
	@Inject AlleleValidator alleleValidator;
	@Inject GeneDAO geneDAO;
	@Inject CrossReferenceService crossReferenceService;
	@Inject SynonymService synonymService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;
	@Inject PersonService personService;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceService referenceService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Allele> update(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleUpdate(uiEntity);
		return new ObjectResponse<Allele>(alleleDAO.persist(dbEntity));
	}
	
	@Override
	@Transactional
	public ObjectResponse<Allele> create(Allele uiEntity) {
		Allele dbEntity = alleleValidator.validateAlleleCreate(uiEntity);
		return new ObjectResponse<Allele>(alleleDAO.persist(dbEntity));
	}

	@Transactional
	public Allele upsert(AlleleDTO dto) throws ObjectUpdateException {
		Allele allele = validateAlleleDTO(dto);
		
		if (allele == null) return null;
		
		return alleleDAO.persist(allele);
	}
	
	public void removeNonUpdatedAlleles(String taxonIds, List<String> alleleCuriesBefore, List<String> alleleCuriesAfter) {
		log.debug("runLoad: After: " + taxonIds + " " + alleleCuriesAfter.size());

		List<String> distinctAfter = alleleCuriesAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + taxonIds + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(alleleCuriesBefore, distinctAfter);
		log.debug("runLoad: Remove: " + taxonIds + " " + curiesToRemove.size());

		log.info("Deleting disease annotations linked to " + curiesToRemove.size() + " unloaded alleles");
		List<String> foundAlleleCuries = new ArrayList<String>();
		for (String curie : curiesToRemove) {
			Allele allele = alleleDAO.find(curie);
			if (allele != null) {
				foundAlleleCuries.add(curie);
			} else {
				log.error("Failed getting allele: " + curie);
			}
		}
		alleleDAO.deleteReferencingDiseaseAnnotations(foundAlleleCuries);
		foundAlleleCuries.forEach(curie -> {delete(curie);});
		log.info("Deletion of disease annotations linked to unloaded alleles finished");
	}
	
	public List<String> getCuriesByTaxonId(String taxonId) {
		List<String> curies = alleleDAO.findAllCuriesByTaxon(taxonId);
		curies.removeIf(Objects::isNull);
		return curies;
	}
	
	private Allele validateAlleleDTO(AlleleDTO dto) throws ObjectValidationException {
		// Check for required fields
		if (StringUtils.isBlank(dto.getCurie()) || StringUtils.isBlank(dto.getTaxon()) || StringUtils.isBlank(dto.getName()) || dto.getInternal() == null) {
			throw new ObjectValidationException(dto, "Entry for allele " + dto.getCurie() + " missing required fields - skipping");
		}

		Allele allele = alleleDAO.find(dto.getCurie());
		if (allele == null) {
			allele = new Allele();
			allele.setCurie(dto.getCurie());
		} 
		
		// Validate taxon ID
		ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.get(dto.getTaxon());
		if (taxonResponse.getEntity() == null) {
			throw new ObjectValidationException(dto, "Invalid taxon ID for allele " + dto.getCurie() + " - skipping");
		}
		allele.setTaxon(taxonResponse.getEntity());
		
		String symbol = null;
		if (StringUtils.isNotBlank(dto.getSymbol())) 
			symbol = dto.getSymbol();
		allele.setSymbol(symbol);
		
		allele.setName(dto.getName());
		
		if (StringUtils.isNotBlank(dto.getCreatedBy())) {
			Person createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedBy());
			allele.setCreatedBy(createdBy);
		}
		if (StringUtils.isNotBlank(dto.getUpdatedBy())) {
			Person updatedBy = personService.fetchByUniqueIdOrCreate(dto.getUpdatedBy());
			allele.setUpdatedBy(updatedBy);
		}
		
		allele.setInternal(dto.getInternal());
		
		Boolean obsolete = false;
		if (dto.getObsolete() != null)
			obsolete = dto.getObsolete();
		allele.setObsolete(obsolete);

		if (StringUtils.isNotBlank(dto.getDateUpdated())) {
			OffsetDateTime dateLastModified;
			try {
				dateLastModified = OffsetDateTime.parse(dto.getDateUpdated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_updated in " + allele.getCurie() + " - skipping");
			}
			allele.setDateUpdated(dateLastModified);
		}

		if (StringUtils.isNotBlank(dto.getDateCreated())) {
			OffsetDateTime creationDate;
			try {
				creationDate = OffsetDateTime.parse(dto.getDateCreated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_created in " + allele.getCurie() + " - skipping");
			}
			allele.setDateCreated(creationDate);
		}
		
		VocabularyTerm inheritenceMode = null;
		if (StringUtils.isNotBlank(dto.getInheritanceMode())) {
			inheritenceMode = vocabularyTermDAO.getTermInVocabulary(dto.getInheritanceMode(), VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY);
			if (inheritenceMode == null) {
				throw new ObjectValidationException(dto, "Invalid inheritance mode (" + dto.getInheritanceMode() + ") in " + allele.getCurie() + " - skipping");
			}
		}
		allele.setInheritanceMode(inheritenceMode);
		
		VocabularyTerm inCollection = null;
		if (StringUtils.isNotBlank(dto.getInCollection())) {
			inCollection = vocabularyTermDAO.getTermInVocabulary(dto.getInCollection(), VocabularyConstants.ALLELE_COLLECTION_VOCABULARY);
			if (inCollection == null) {
				throw new ObjectValidationException(dto, "Invalid collection (" + dto.getInCollection() + ") in " + allele.getCurie() + " - skipping");
			}
		}
		allele.setInCollection(inCollection);
		
		VocabularyTerm sequencingStatus = null;
		if (StringUtils.isNotBlank(dto.getSequencingStatus())) {
			sequencingStatus = vocabularyTermDAO.getTermInVocabulary(dto.getSequencingStatus(), VocabularyConstants.SEQUENCING_STATUS_VOCABULARY);
			if (sequencingStatus == null) {
				throw new ObjectValidationException(dto, "Invalid sequencing status (" + dto.getSequencingStatus() + ") in " + allele.getCurie() + " - skipping");
			}
		}
		allele.setSequencingStatus(sequencingStatus);

		if (dto.getIsExtinct() != null)
			allele.setIsExtinct(dto.getIsExtinct());
		
		List<Reference> references = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getReferences())) {
			for (String publicationId : dto.getReferences()) {
				Reference reference = referenceDAO.find(publicationId);
				if (reference == null || reference.getObsolete()) {
					reference = referenceService.retrieveFromLiteratureService(publicationId);
					if (reference == null) {
						throw new ObjectValidationException(dto, "Invalid reference (" + publicationId + ") in allele " + allele.getCurie() + " - skipping");
					}
				}
				references.add(reference);
			}
		}
		allele.setReferences(references);
		
		return allele;
	}

}
