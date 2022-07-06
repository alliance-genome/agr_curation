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

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.AffectedGenomicModelValidator;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AffectedGenomicModelService extends BaseCrudService<AffectedGenomicModel, AffectedGenomicModelDAO> {

	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;
	@Inject
	CrossReferenceService crossReferenceService;
	@Inject
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	SynonymService synonymService;
	@Inject
	AlleleDAO alleleDAO;
	@Inject
	AffectedGenomicModelValidator affectedGenomicModelValidator;
	@Inject
	NcbiTaxonTermDAO ncbiTaxonTermDAO;
	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;
	@Inject
	PersonService personService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(affectedGenomicModelDAO);
	}

	@Transactional
	@Override
	public ObjectResponse<AffectedGenomicModel> create(AffectedGenomicModel entity) {
		NCBITaxonTerm term = ncbiTaxonTermDAO.find(entity.getTaxon().getCurie());
		entity.setTaxon(term);
		if (CollectionUtils.isNotEmpty(entity.getCrossReferences())) {
			List<CrossReference> refs = new ArrayList<>();
			entity.getCrossReferences().forEach(crossReference -> {
				CrossReference reference = new CrossReference();
				reference.setCurie(crossReference.getCurie());
				crossReferenceDAO.persist(reference);
				refs.add(reference);
			});
			entity.setCrossReferences(refs);
		}
		AffectedGenomicModel object = dao.persist(entity);
		ObjectResponse<AffectedGenomicModel> ret = new ObjectResponse<>(object);
		return ret;
	}

	@Override
	@Transactional
	public ObjectResponse<AffectedGenomicModel> update(AffectedGenomicModel uiEntity) {
		log.info(authenticatedPerson);
		AffectedGenomicModel dbEntity = affectedGenomicModelValidator.validateAnnotation(uiEntity);
		return new ObjectResponse<>(affectedGenomicModelDAO.persist(dbEntity));
	}


	@Transactional
	public AffectedGenomicModel upsert(AffectedGenomicModelDTO dto) throws ObjectUpdateException {
		AffectedGenomicModel agm = validateAffectedGenomicModelDTO(dto);
		
		if (agm == null) return null;
		
		return affectedGenomicModelDAO.persist(agm);
	}
	
	public void removeNonUpdatedAgms(String taxonIds, List<String> agmCuriesBefore, List<String> agmCuriesAfter) {
		log.debug("runLoad: After: " + taxonIds + " " + agmCuriesAfter.size());

		List<String> distinctAfter = agmCuriesAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + taxonIds + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(agmCuriesBefore, distinctAfter);
		log.debug("runLoad: Remove: " + taxonIds + " " + curiesToRemove.size());

		log.info("Deleting disease annotations linked to " + curiesToRemove.size() + " unloaded AGMs");
		List<String> foundAgmCuries = new ArrayList<String>();
		for (String curie : curiesToRemove) {
			AffectedGenomicModel agm = affectedGenomicModelDAO.find(curie);
			if (agm != null) {
				foundAgmCuries.add(curie);
			} else {
				log.error("Failed getting AGM: " + curie);
			}
		}
		affectedGenomicModelDAO.deleteReferencingDiseaseAnnotations(foundAgmCuries);
		foundAgmCuries.forEach(curie -> {delete(curie);});
		log.info("Deletion of disease annotations linked to unloaded AGMs finished");
	}
	
	public List<String> getCuriesByTaxonId(String taxonId) {
		List<String> curies = affectedGenomicModelDAO.findAllCuriesByTaxon(taxonId);
		curies.removeIf(Objects::isNull);
		return curies;
	}
	
	private AffectedGenomicModel validateAffectedGenomicModelDTO(AffectedGenomicModelDTO dto) throws ObjectValidationException {
		// Check for required fields
		if (dto.getCurie() == null || dto.getTaxon() == null) {
			throw new ObjectValidationException(dto, "Entry for allele " + dto.getCurie() + " missing required fields - skipping");
		}

		AffectedGenomicModel agm = affectedGenomicModelDAO.find(dto.getCurie());
		if (agm == null) {
			agm = new AffectedGenomicModel();
			agm.setCurie(dto.getCurie());
		} 
		
		// Validate taxon ID
		ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.get(dto.getTaxon());
		if (taxonResponse.getEntity() == null) {
			throw new ObjectValidationException(dto, "Invalid taxon ID for allele " + dto.getCurie() + " - skipping");
		}
		agm.setTaxon(taxonResponse.getEntity());
		
		if (dto.getName() != null) agm.setName(dto.getName());
		
		if (dto.getCreatedBy() != null) {
			Person createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedBy());
			agm.setCreatedBy(createdBy);
		}
		if (dto.getUpdatedBy() != null) {
			Person modifiedBy = personService.fetchByUniqueIdOrCreate(dto.getUpdatedBy());
			agm.setUpdatedBy(modifiedBy);
		}
		
		agm.setInternal(dto.getInternal());
		agm.setObsolete(dto.getObsolete());

		if (dto.getDateUpdated() != null) {
			OffsetDateTime dateLastModified;
			try {
				dateLastModified = OffsetDateTime.parse(dto.getDateUpdated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_updated in " + agm.getCurie() + " - skipping");
			}
			agm.setDateUpdated(dateLastModified);
		}

		if (dto.getDateCreated() != null) {
			OffsetDateTime creationDate;
			try {
				creationDate = OffsetDateTime.parse(dto.getDateCreated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_created in " + agm.getCurie() + " - skipping");
			}
			agm.setDateCreated(creationDate);
		}
		
		return agm;
	}
}
