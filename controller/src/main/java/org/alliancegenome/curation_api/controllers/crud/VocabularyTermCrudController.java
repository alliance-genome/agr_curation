package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.interfaces.crud.VocabularyTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.lang3.exception.ExceptionUtils;

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
	public ObjectResponse<VocabularyTerm> delete(Long id) {
		ObjectResponse<VocabularyTerm> ret;
		try {
			ret = vocabularyTermService.delete(id);
		} catch (ConstraintViolationException cve) {
			ret = new ObjectResponse<>();
			ret.setErrorMessage("Could not delete vocabulary term: [" + id + "]");
			throw new ApiErrorException(ret);
		} catch (Exception p) {
			String message = ExceptionUtils.getRootCauseMessage(p);
			message = "Vocabulary Term ["+get(id).getEntity().getName() + "] is still being used. \r\n" + message;
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
