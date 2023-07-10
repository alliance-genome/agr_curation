package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.VocabularyTermValidator;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

@RequestScoped
public class VocabularyTermService extends BaseEntityCrudService<VocabularyTerm, VocabularyTermDAO> {

	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	VocabularyTermValidator vocabularyTermValidator;

	HashMap<String, Date> termRequestMap = new HashMap<>();
	HashMap<String, HashMap<String, VocabularyTerm>> vocabTermCacheMap = new HashMap<>();
	
	HashMap<String, Date> termSetRequestMap = new HashMap<>();
	HashMap<String, HashMap<String, VocabularyTerm>> vocabTermSetCacheMap = new HashMap<>();
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(vocabularyTermDAO);
	}

	public ObjectResponse<VocabularyTerm> getTermInVocabulary(String vocabularyName, String termName) {

		VocabularyTerm term = null;
		
		if(termRequestMap.get(vocabularyName) != null) {
			HashMap<String, VocabularyTerm> termMap = vocabTermCacheMap.get(vocabularyName);
			
			if(termMap == null) {
				Log.debug("Vocab not cached, caching vocab: " + vocabularyName);
				termMap = new HashMap<>();
				vocabTermCacheMap.put(vocabularyName, termMap);
			}

			if(termMap.containsKey(termName)) {
				term = termMap.get(termName);
			} else {
				Log.debug("Term not cached, caching term: " + vocabularyName + "(" + termName + ")");
				term = getTermInVocabularyFromDB(vocabularyName, termName);
				termMap.put(termName, term);
			}
		} else {
			term = getTermInVocabularyFromDB(vocabularyName, termName);
			termRequestMap.put(vocabularyName, new Date());
		}

		ObjectResponse<VocabularyTerm> response = new ObjectResponse<>();
		response.setEntity(term);
		return response;
		
	}

	public ObjectResponse<VocabularyTerm> getTermInVocabularyTermSet(String vocabularyTermSetName, String termName) {

		VocabularyTerm term = null;
		
		if(termSetRequestMap.get(vocabularyTermSetName) != null) {
			HashMap<String, VocabularyTerm> termMap = vocabTermSetCacheMap.get(vocabularyTermSetName);
			
			if(termMap == null) {
				Log.debug("Vocab not cached, caching vocab: " + vocabularyTermSetName);
				termMap = new HashMap<>();
				vocabTermSetCacheMap.put(vocabularyTermSetName, termMap);
			}

			if(termMap.containsKey(termName)) {
				term = termMap.get(termName);
			} else {
				Log.debug("Term not cached, caching term: " + vocabularyTermSetName + "(" + termName + ")");
				term = getTermInVocabularyTermSetFromDB(vocabularyTermSetName, termName);
				termMap.put(termName, term);
			}
		} else {
			term = getTermInVocabularyTermSetFromDB(vocabularyTermSetName, termName);
			termSetRequestMap.put(vocabularyTermSetName, new Date());
		}

		ObjectResponse<VocabularyTerm> response = new ObjectResponse<VocabularyTerm>();
		response.setEntity(term);
		return response;
		
	}
	
	private VocabularyTerm getTermInVocabularyFromDB(String vocabularyName, String termName) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", termName);
		params.put("vocabulary.name", vocabularyName);
		SearchResponse<VocabularyTerm> resp = vocabularyTermDAO.findByParams(params);
		return resp.getSingleResult();
	}
	
	private VocabularyTerm getTermInVocabularyTermSetFromDB(String vocabularyTermSetName, String termName) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", termName);
		params.put("vocabularyTermSets.name", vocabularyTermSetName);

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
