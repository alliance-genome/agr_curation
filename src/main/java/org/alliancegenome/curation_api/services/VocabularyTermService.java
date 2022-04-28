package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.VocabularyTermValidator;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

@RequestScoped
public class VocabularyTermService extends BaseCrudService<VocabularyTerm, VocabularyTermDAO> {

	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	VocabularyTermValidator vocabularyTermValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(vocabularyTermDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<VocabularyTerm> update(VocabularyTerm uiEntity) {
		VocabularyTerm dbEntity = vocabularyTermValidator.validateVocabularyTerm(uiEntity);
		return new ObjectResponse<>(vocabularyTermDAO.persist(dbEntity));
	}

	public ObjectResponse<VocabularyTerm> deleteSingle(Long id) {
		ObjectResponse<VocabularyTerm> ret;
		try {
			ret = deleteRecordInTX(id);
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

	@Transactional(Transactional.TxType.REQUIRES_NEW)
	private ObjectResponse<VocabularyTerm> deleteRecordInTX(Long id) {
		VocabularyTerm object = vocabularyTermDAO.remove(id);
		return new ObjectResponse<>(object);
	}

}
