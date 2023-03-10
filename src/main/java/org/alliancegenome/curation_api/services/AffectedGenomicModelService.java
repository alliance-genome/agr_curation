package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.AffectedGenomicModelValidator;
import org.alliancegenome.curation_api.services.validation.dto.AffectedGenomicModelDTOValidator;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AffectedGenomicModelService extends BaseDTOCrudService<AffectedGenomicModel, AffectedGenomicModelDTO, AffectedGenomicModelDAO> {

	@Inject
	AffectedGenomicModelDAO agmDAO;
	@Inject
	AlleleDAO alleleDAO;
	@Inject
	AffectedGenomicModelValidator agmValidator;
	@Inject
	AffectedGenomicModelDTOValidator agmDtoValidator;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;
	@Inject
	PersonService personService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmDAO);
	}
	
	@Override
	@Transactional
	public ObjectResponse<AffectedGenomicModel> update(AffectedGenomicModel uiEntity) {
		AffectedGenomicModel dbEntity = agmDAO.persist(agmValidator.validateAffectedGenomicModelUpdate(uiEntity));
		return new ObjectResponse<AffectedGenomicModel>(dbEntity);
	}
	
	@Override
	@Transactional
	public ObjectResponse<AffectedGenomicModel> create(AffectedGenomicModel uiEntity) {
		AffectedGenomicModel dbEntity = agmDAO.persist(agmValidator.validateAffectedGenomicModelCreate(uiEntity));
		return new ObjectResponse<AffectedGenomicModel>(dbEntity);
	}

	@Transactional
	public AffectedGenomicModel upsert(AffectedGenomicModelDTO dto, String dataType) throws ObjectUpdateException {
		AffectedGenomicModel agm = agmDtoValidator.validateAffectedGenomicModelDTO(dto, dataType);

		if (agm == null)
			return null;

		return agmDAO.persist(agm);
	}

	public void removeOrDeprecateNonUpdatedAgms(String speciesNames, List<String> agmCuriesBefore, List<String> agmCuriesAfter, String dataType) {
		log.debug("runLoad: After: " + speciesNames + " " + agmCuriesAfter.size());

		List<String> distinctAfter = agmCuriesAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + speciesNames + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(agmCuriesBefore, distinctAfter);
		log.debug("runLoad: Remove: " + speciesNames + " " + curiesToRemove.size());

		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of disease annotations linked to unloaded " + speciesNames + " AGMs", curiesToRemove.size());
		for (String curie : curiesToRemove) {
			removeOrDeprecateNonUpdatedAgm(curie, dataType);
			ph.progressProcess();
		}
		ph.finishProcess();
	}

	@Transactional
	public void removeOrDeprecateNonUpdatedAgm(String curie, String dataType) {
		AffectedGenomicModel agm = agmDAO.find(curie);
		if (agm != null) {
			List<Long> referencingDAIds = agmDAO.findReferencingDiseaseAnnotations(curie);
			Boolean anyReferencingDAs = false;
			for (Long daId : referencingDAIds) {
				DiseaseAnnotation referencingDA = diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(daId, false, "AGM", true);
				if (referencingDA != null)
					anyReferencingDAs = true;
			}

			if (anyReferencingDAs) {
				agm.setUpdatedBy(personService.fetchByUniqueIdOrCreate(dataType + " AGM bulk upload"));
				agm.setDateUpdated(OffsetDateTime.now());
				agm.setObsolete(true);
				agmDAO.persist(agm);
			} else {
				agmDAO.remove(curie);
			}
		} else {
			log.error("Failed getting AGM: " + curie);
		}
	}
	
	public List<String> getCuriesBySpeciesName(String speciesName) {
		List<String> curies = agmDAO.findAllCuriesBySpeciesName(speciesName);
		curies.removeIf(Objects::isNull);
		return curies;
	}

}
