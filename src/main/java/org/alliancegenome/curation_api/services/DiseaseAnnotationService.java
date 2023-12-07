package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationUniqueIdUpdateHelper;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseEntityCrudService<DiseaseAnnotation, DiseaseAnnotationDAO> {

	@Inject
	DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject
	PersonService personService;
	@Inject
	DiseaseAnnotationUniqueIdUpdateHelper uniqueIdUpdateHelper;
	@Inject
	PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(diseaseAnnotationDAO);
	}
	
	@Override
	public ObjectResponse<DiseaseAnnotation> get(String identifier) {
		SearchResponse<DiseaseAnnotation> ret = findByField("curie", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<DiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modEntityId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<DiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modInternalId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<DiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("uniqueId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<DiseaseAnnotation>(ret.getResults().get(0));
				
		return new ObjectResponse<DiseaseAnnotation>();
	}
	
	@Override
	@Transactional
	public ObjectResponse<DiseaseAnnotation> delete(Long id) {
		deprecateOrDeleteAnnotationAndNotes(id, true, "Disease annotation DELETE API call", false);
		ObjectResponse<DiseaseAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	@Transactional
	public DiseaseAnnotation deprecateOrDeleteAnnotationAndNotes(Long id, Boolean throwApiError, String loadDescription, Boolean deprecateAnnotation) {
		DiseaseAnnotation annotation = diseaseAnnotationDAO.find(id);

		if (annotation == null) {
			String errorMessage = "Could not find Disease Annotation with id: " + id;
			if (throwApiError) {
				ObjectResponse<DiseaseAnnotation> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			log.error(errorMessage);
			return null;
		}

		if (deprecateAnnotation) {
			if (!annotation.getObsolete()) {
				annotation.setObsolete(true);
				if (authenticatedPerson.getId() != null) {
					annotation.setUpdatedBy(personDAO.find(authenticatedPerson.getId()));
				} else {
					annotation.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
				}
				annotation.setDateUpdated(OffsetDateTime.now());
				return diseaseAnnotationDAO.persist(annotation);
			} else {
				return annotation;
			}
		} else {
			diseaseAnnotationDAO.remove(id);
		}

		return null;
	}

	public void updateUniqueIds() {
		uniqueIdUpdateHelper.updateDiseaseAnnotationUniqueIds();
	}

	public List<Long> getAllReferencedConditionRelationIds() {
		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		
		List<Long> daIds = diseaseAnnotationDAO.findAllIds().getResults();
		pdh.startProcess("Checking DAs for referenced Conditions ", daIds.size());
		
		List<Long> conditionRelationIds = new ArrayList<>();
		daIds.forEach(daId -> {
			DiseaseAnnotation annotation = diseaseAnnotationDAO.find(daId);
			if (CollectionUtils.isNotEmpty(annotation.getConditionRelations())) {
				List<Long> crIds = annotation.getConditionRelations().stream().map(ConditionRelation::getId).collect(Collectors.toList());
				conditionRelationIds.addAll(crIds);
			}
			pdh.progressProcess();
		});
		pdh.finishProcess();
		
		return conditionRelationIds;
	}

}
