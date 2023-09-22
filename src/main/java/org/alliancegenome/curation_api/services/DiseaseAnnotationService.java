package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationUniqueIdUpdateHelper;
import org.apache.commons.collections.CollectionUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseEntityCrudService<DiseaseAnnotation, DiseaseAnnotationDAO> {

	@Inject
	DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject
	NoteService noteService;
	@Inject
	PersonService personService;
	@Inject
	DataProviderDAO dataProviderDAO;
	@Inject
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	DiseaseAnnotationUniqueIdUpdateHelper uniqueIdUpdateHelper;

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
				if (authenticatedPerson.getOktaEmail() != null) {
					annotation.setUpdatedBy(personService.findPersonByOktaEmail(authenticatedPerson.getOktaEmail()));
				} else {
					annotation.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
				}
				annotation.setDateUpdated(OffsetDateTime.now());
				return diseaseAnnotationDAO.persist(annotation);
			} else {
				return annotation;
			}
		} else {
			List<Note> notesToDelete = annotation.getRelatedNotes();
			diseaseAnnotationDAO.remove(id);

			if (CollectionUtils.isNotEmpty(notesToDelete))
				notesToDelete.forEach(note -> noteService.delete(note.getId()));
		}

		return null;
	}

	public void updateUniqueIds() {
		uniqueIdUpdateHelper.updateDiseaseAnnotationUniqueIds();
	}

}
