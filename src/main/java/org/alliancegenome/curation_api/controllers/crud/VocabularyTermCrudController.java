package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.interfaces.crud.VocabularyTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.lang3.exception.ExceptionUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

@RequestScoped
public class VocabularyTermCrudController extends BaseEntityCrudController<VocabularyTermService, VocabularyTerm, VocabularyTermDAO> implements VocabularyTermCrudInterface {

	@Inject
	VocabularyTermService vocabularyTermService;

	@Override
	@PostConstruct
	protected void init() {
		setService(vocabularyTermService);
	}

	@Override
	public ObjectResponse<VocabularyTerm> deleteById(Long id) {
		ObjectResponse<VocabularyTerm> ret;
		try {
			ret = vocabularyTermService.deleteById(id);
		} catch (ConstraintViolationException cve) {
			ret = new ObjectResponse<>();
			ret.setErrorMessage("Could not delete vocabulary term: [" + id + "]");
			throw new ApiErrorException(ret);
		} catch (Exception p) {
			String message = ExceptionUtils.getRootCauseMessage(p);
			message = "Vocabulary Term [" + vocabularyTermService.getById(id).getEntity().getName() + "] is still being used. \r\n" + message;
			ret = new ObjectResponse<>();
			ret.setErrorMessage(message);
			throw new ApiErrorException(ret);
		}
		return ret;
	}

	@Override
	public ObjectResponse<VocabularyTerm> getTermInVocabulary(String vocabularyName, String termName) {
		return vocabularyTermService.getTermInVocabulary(vocabularyName, termName);
	}

	@Override
	public ObjectResponse<VocabularyTerm> getTermInVocabularyTermSet(String vocabularyTermSetName, String termName) {
		return vocabularyTermService.getTermInVocabularyTermSet(vocabularyTermSetName, termName);
	}

}
