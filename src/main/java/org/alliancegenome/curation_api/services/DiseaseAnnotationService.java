package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseEntityCrudService<DiseaseAnnotation, DiseaseAnnotationDAO> {


	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject NoteService noteService;
	@Inject LoggedInPersonService loggedInPersonService;
	@Inject PersonService personService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(diseaseAnnotationDAO);
	}

	// The following methods are for bulk validation

	public void removeNonUpdatedAnnotations(String taxonId, List<Long> annotationIdsBefore, List<Long> annotationIdsAfter) {
		log.debug("runLoad: After: " + taxonId + " " + annotationIdsAfter.size());

		List<Long> distinctAfter = annotationIdsAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + taxonId + " " + distinctAfter.size());

		List<Long> idsToRemove = ListUtils.subtract(annotationIdsBefore, distinctAfter);
		log.debug("runLoad: Remove: " + taxonId + " " + idsToRemove.size());

		for (Long id : idsToRemove) {
			deprecateOrDeleteAnnotationAndNotes(id, false);
		}
	}

	@Override
	@Transactional
	public ObjectResponse<DiseaseAnnotation> delete(Long id) {
		deprecateOrDeleteAnnotationAndNotes(id, true);
		ObjectResponse<DiseaseAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	@Transactional
	public Boolean deprecateOrDeleteAnnotationAndNotes(Long id, Boolean throwApiError) {
		DiseaseAnnotation annotation = diseaseAnnotationDAO.find(id);

		if (annotation == null) {
			String errorMessage = "Could not find Disease Annotation with id: " + id;
			if (throwApiError) {
				ObjectResponse<DiseaseAnnotation> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			log.error(errorMessage);
			return false;
		}
		
		Boolean madePublic = true; //TODO: check boolean field once in place
		if (madePublic) {
			annotation.setObsolete(true);
			if (authenticatedPerson.getOktaEmail() != null) {
				annotation.setUpdatedBy(loggedInPersonService.findLoggedInPersonByOktaEmail(authenticatedPerson.getOktaEmail()));
			} else {
				annotation.setUpdatedBy(personService.fetchByUniqueIdOrCreate(annotation.getDataProvider().getAbbreviation() + " bulk upload"));
			}
			annotation.setDateUpdated(OffsetDateTime.now());
			diseaseAnnotationDAO.persist(annotation);
		} else {
			List<Note> notesToDelete = annotation.getRelatedNotes();
			diseaseAnnotationDAO.remove(id);
		
			if (CollectionUtils.isNotEmpty(notesToDelete))
				annotation.getRelatedNotes().forEach(note -> noteService.delete(note.getId()));
		}
		
		return madePublic;
	}
	
}
