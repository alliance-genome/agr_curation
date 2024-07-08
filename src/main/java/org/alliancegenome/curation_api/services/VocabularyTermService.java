package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.VocabularyTermValidator;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class VocabularyTermService extends BaseEntityCrudService<VocabularyTerm, VocabularyTermDAO> {

	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject VocabularyTermValidator vocabularyTermValidator;

	HashMap<String, Date> termRequestMap = new HashMap<>();
	HashMap<String, HashMap<String, VocabularyTerm>> vocabTermCacheMap = new HashMap<>();

	HashMap<String, Date> termSetRequestMap = new HashMap<>();
	HashMap<String, HashMap<String, VocabularyTerm>> vocabTermSetCacheMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(vocabularyTermDAO);
	}

	public ObjectResponse<VocabularyTerm> getTermInVocabulary(String vocabularyLabel, String termName) {

		VocabularyTerm term = null;

		if (termRequestMap.containsKey(vocabularyLabel)) {
			HashMap<String, VocabularyTerm> termMap = vocabTermCacheMap.get(vocabularyLabel);

			if (termMap == null) {
				Log.debug("Vocab not cached, caching vocab: " + vocabularyLabel);
				termMap = new HashMap<>();
				vocabTermCacheMap.put(vocabularyLabel, termMap);
			}

			if (termMap.containsKey(termName)) {
				term = termMap.get(termName);
			} else {
				Log.debug("Term not cached, caching term: " + vocabularyLabel + "(" + termName + ")");
				term = getTermInVocabularyFromDB(vocabularyLabel, termName);
				if (term != null) {
					term.getSynonyms().size();
				}
				termMap.put(termName, term);
			}
		} else {
			term = getTermInVocabularyFromDB(vocabularyLabel, termName);
			termRequestMap.put(vocabularyLabel, new Date());
		}

		ObjectResponse<VocabularyTerm> response = new ObjectResponse<>();
		response.setEntity(term);
		return response;

	}

	public ObjectResponse<VocabularyTerm> getTermInVocabularyTermSet(String vocabularyTermSetLabel, String termName) {

		VocabularyTerm term = null;

		if (termSetRequestMap.containsKey(vocabularyTermSetLabel)) {
			HashMap<String, VocabularyTerm> termMap = vocabTermSetCacheMap.get(vocabularyTermSetLabel);

			if (termMap == null) {
				Log.debug("Vocab not cached, caching vocab: " + vocabularyTermSetLabel);
				termMap = new HashMap<>();
				vocabTermSetCacheMap.put(vocabularyTermSetLabel, termMap);
			}

			if (termMap.containsKey(termName)) {
				term = termMap.get(termName);
			} else {
				Log.debug("Term not cached, caching term: " + vocabularyTermSetLabel + "(" + termName + ")");
				term = getTermInVocabularyTermSetFromDB(vocabularyTermSetLabel, termName);
				if (term != null) {
					term.getSynonyms().size();
				}
				termMap.put(termName, term);
			}
		} else {
			term = getTermInVocabularyTermSetFromDB(vocabularyTermSetLabel, termName);
			termSetRequestMap.put(vocabularyTermSetLabel, new Date());
		}

		ObjectResponse<VocabularyTerm> response = new ObjectResponse<VocabularyTerm>();
		response.setEntity(term);
		return response;

	}

	private VocabularyTerm getTermInVocabularyFromDB(String vocabularyLabel, String termName) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", termName);
		if (vocabularyLabel != null) {
			params.put("vocabulary.vocabularyLabel", vocabularyLabel);
		}
		SearchResponse<VocabularyTerm> resp = vocabularyTermDAO.findByParams(params);
		return resp.getSingleResult();
	}

	private VocabularyTerm getTermInVocabularyTermSetFromDB(String vocabularyTermSetLabel, String termName) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", termName);
		if (vocabularyTermSetLabel != null) {
			params.put("vocabularyTermSets.vocabularyLabel", vocabularyTermSetLabel);
		}
		SearchResponse<VocabularyTerm> resp = vocabularyTermDAO.findByParams(params);
		return resp.getSingleResult();
	}

	@Override
	@Transactional
	public ObjectResponse<VocabularyTerm> create(VocabularyTerm uiEntity) {
		VocabularyTerm dbEntity = vocabularyTermValidator.validateVocabularyTermCreate(uiEntity);
		return new ObjectResponse<>(vocabularyTermDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<VocabularyTerm> update(VocabularyTerm uiEntity) {
		VocabularyTerm dbEntity = vocabularyTermValidator.validateVocabularyTermUpdate(uiEntity);
		return new ObjectResponse<>(vocabularyTermDAO.persist(dbEntity));
	}

}
