package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.AffectedGenomicModelValidator;
import org.alliancegenome.curation_api.services.validation.dto.AffectedGenomicModelDTOValidator;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AffectedGenomicModelService extends BaseDTOCrudService<AffectedGenomicModel, AffectedGenomicModelDTO, AffectedGenomicModelDAO> {

	@Inject AffectedGenomicModelDAO affectedGenomicModelDAO;
	@Inject CrossReferenceService crossReferenceService;
	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject SynonymService synonymService;
	@Inject AlleleDAO alleleDAO;
	@Inject AffectedGenomicModelValidator affectedGenomicModelValidator;
	@Inject AffectedGenomicModelDTOValidator affectedGenomicModelDtoValidator;
	@Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PersonService personService;
	
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
		AffectedGenomicModel agm = affectedGenomicModelDtoValidator.validateAffectedGenomicModelDTO(dto);
		
		if (agm == null) return null;
		
		return affectedGenomicModelDAO.persist(agm);
	}
	
	@Transactional
	public void removeNonUpdatedAgms(String taxonIds, List<String> agmCuriesBefore, List<String> agmCuriesAfter) {
		log.debug("runLoad: After: " + taxonIds + " " + agmCuriesAfter.size());

		List<String> distinctAfter = agmCuriesAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + taxonIds + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(agmCuriesBefore, distinctAfter);
		log.debug("runLoad: Remove: " + taxonIds + " " + curiesToRemove.size());

		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of disease annotations linked to unloaded " + taxonIds + " AGMs", curiesToRemove.size());
		for (String curie : curiesToRemove) {
			AffectedGenomicModel agm = affectedGenomicModelDAO.find(curie);
			if (agm != null) {
				List<Long> referencingDAIds = affectedGenomicModelDAO.findReferencingDiseaseAnnotations(curie);
				Boolean anyPublicReferencingDAs = false;
				for (Long daId : referencingDAIds) {
					Boolean daMadePublic = diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(daId, false);
					if (daMadePublic)
						anyPublicReferencingDAs = true;
				}
				
				if (anyPublicReferencingDAs) {
					agm.setUpdatedBy(personService.fetchByUniqueIdOrCreate("Gene bulk upload"));
					agm.setDateUpdated(OffsetDateTime.now());
					agm.setObsolete(true);
					affectedGenomicModelDAO.persist(agm);
				} else {
					affectedGenomicModelDAO.remove(curie);
				}
			} else {
				log.error("Failed getting AGM: " + curie);
			}
			ph.progressProcess();
		}
		ph.finishProcess();
	}
	
	public List<String> getCuriesByTaxonId(String taxonId) {
		List<String> curies = affectedGenomicModelDAO.findAllCuriesByTaxon(taxonId);
		curies.removeIf(Objects::isNull);
		return curies;
	}
	
	
}
