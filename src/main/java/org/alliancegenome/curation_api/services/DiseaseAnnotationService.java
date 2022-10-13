package org.alliancegenome.curation_api.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseEntityCrudService<DiseaseAnnotation, DiseaseAnnotationDAO> {


	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject NoteService noteService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(diseaseAnnotationDAO);
	}

	// The following methods are for bulk validation

	public void removeNonUpdatedAnnotations(String taxonId, List<String> annotationIdsBefore, List<String> annotationIdsAfter) {
		log.debug("runLoad: After: " + taxonId + " " + annotationIdsAfter.size());

		List<String> distinctAfter = annotationIdsAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + taxonId + " " + distinctAfter.size());

		List<String> idsToRemove = ListUtils.subtract(annotationIdsBefore, distinctAfter);
		log.debug("runLoad: Remove: " + taxonId + " " + idsToRemove.size());

		for (String id : idsToRemove) {
			SearchResponse<DiseaseAnnotation> da = diseaseAnnotationDAO.findByField("uniqueId", id);
			if (da != null && da.getTotalResults() == 1) {
				List<Long> noteIdsToDelete = da.getResults().get(0).getRelatedNotes().stream().map(Note::getId).collect(Collectors.toList());
				delete(da.getResults().get(0).getId());
				for (Long noteId : noteIdsToDelete) {
					noteService.delete(noteId);
				}
			} else {
				log.error("Failed getting annotation: " + id);
			}
		}
	}

	@Transactional
	public ObjectResponse<DiseaseAnnotation> deleteNotes(Long id) {
		SearchResponse<DiseaseAnnotation> response = dao.searchByField(new Pagination(0, 20), "id", Long.toString(id));

		DiseaseAnnotation singleResult = response.getSingleResult();
		if (singleResult == null) {
			ObjectResponse<DiseaseAnnotation> oResponse = new ObjectResponse<>();
			oResponse.addErrorMessage("id", "Could not find Disease Annotation with id: " + id);
			throw new ApiErrorException(oResponse);
		}
		// remove notes
		if (CollectionUtils.isNotEmpty(singleResult.getRelatedNotes())) {
			singleResult.getRelatedNotes().forEach(note -> noteService.delete(note.getId()));
		}
		singleResult.setRelatedNotes(null);
		if (singleResult.getSingleReference() != null) {
			singleResult.setSingleReference(null);
		}

		return new ObjectResponse<>(singleResult);
	}

}
