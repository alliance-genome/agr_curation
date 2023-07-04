package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.DataProviderDAO;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
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
	LoggedInPersonService loggedInPersonService;
	@Inject
	PersonService personService;
	@Inject
	DataProviderDAO dataProviderDAO;
	@Inject
	CrossReferenceDAO crossReferenceDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(diseaseAnnotationDAO);
	}
	
	@Override
	public ObjectResponse<DiseaseAnnotation> get(String identifier) {
		SearchResponse<DiseaseAnnotation> ret = findByField("modEntityId", identifier);
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
					annotation.setUpdatedBy(loggedInPersonService.findLoggedInPersonByOktaEmail(authenticatedPerson.getOktaEmail()));
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
				annotation.getRelatedNotes().forEach(note -> noteService.delete(note.getId()));
		}

		return null;
	}
	
	
	// TODO remove code below this once SCRUM-3037 resolved
	private Set<Long> xrefIdsToRemove = new HashSet<>();
	private Set<Long> dataProviderIdsToRemove = new HashSet<>();

	public void resetDataProviders() {
		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		int page = 0;
		int limit = 500;
		Pagination pagination = new Pagination(page, limit);

		Boolean allSynced = false;
		while (!allSynced) {
			pagination.setPage(page);
			SearchResponse<String> response = diseaseAnnotationDAO.findAllIds(pagination);
			if (page == 0)
				pdh.startProcess("DiseaseAnnotation data provider reset", response.getTotalResults());
			for (String daId : response.getResults()) {
				resetDataProvider(Long.parseLong(daId));
				pdh.progressProcess();
			}
			page = page + 1;
			int nrSynced = limit * page;
			if (nrSynced > response.getTotalResults().intValue()) {
				nrSynced = response.getTotalResults().intValue();
				allSynced = true;
			}
		}
		pdh.finishProcess();
		
		for (Long dataProviderId : dataProviderIdsToRemove) {
			dataProviderDAO.remove(dataProviderId);
		}
		
		for (Long xrefId : xrefIdsToRemove) {
			crossReferenceDAO.remove(xrefId);
		}
	}
	
	@Transactional
	public void resetDataProvider(Long id) {
		DiseaseAnnotation annotation = diseaseAnnotationDAO.find(id);
		if (annotation == null)
			return;
		
		DataProvider newDataProvider = new DataProvider();
		DataProvider oldDataProvider = annotation.getDataProvider();
		newDataProvider.setSourceOrganization(oldDataProvider.getSourceOrganization());	
		annotation.setDataProvider(dataProviderDAO.persist(newDataProvider));
		
		DataProvider oldSecondaryDataProvider = null;
		if (annotation.getSecondaryDataProvider() != null) {
			DataProvider newSecondaryDataProvider = new DataProvider();
			oldSecondaryDataProvider = annotation.getDataProvider();
			newSecondaryDataProvider.setSourceOrganization(oldSecondaryDataProvider.getSourceOrganization());	
			annotation.setSecondaryDataProvider(dataProviderDAO.persist(newSecondaryDataProvider));
		}
		diseaseAnnotationDAO.merge(annotation);
		
		Long xrefId = oldDataProvider.getCrossReference() == null ? null : oldDataProvider.getCrossReference().getId();
		dataProviderIdsToRemove.add(oldDataProvider.getId());
		if (xrefId != null)
			xrefIdsToRemove.add(xrefId);
		
		if (oldSecondaryDataProvider != null) {
			Long secondaryXrefId = oldSecondaryDataProvider.getCrossReference() == null ? null : oldSecondaryDataProvider.getCrossReference().getId();
			dataProviderIdsToRemove.add(oldSecondaryDataProvider.getId());
			if (secondaryXrefId != null)
				xrefIdsToRemove.add(secondaryXrefId);
		}
	}

}
