package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.VocabularyTermValidator;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;

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

	public ObjectResponse<VocabularyTerm> getTermInVocabulary(String vocabularyLabel, String termLabel) {

		VocabularyTerm term = null;
		
		if(termRequestMap.get(vocabularyLabel) != null) {
			HashMap<String, VocabularyTerm> termMap = vocabTermCacheMap.get(vocabularyLabel);
			
			if(termMap == null) {
				Log.debug("Vocab not cached, caching vocab: " + vocabularyLabel);
				termMap = new HashMap<>();
				vocabTermCacheMap.put(vocabularyLabel, termMap);
			}

			if(termMap.containsKey(termLabel)) {
				term = termMap.get(termLabel);
			} else {
				Log.debug("Term not cached, caching term: " + vocabularyLabel + "(" + termLabel + ")");
				term = getTermInVocabularyFromDB(vocabularyLabel, termLabel);
				if (term != null)
					term.getSynonyms().size();
				termMap.put(termLabel, term);
			}
		} else {
			term = getTermInVocabularyFromDB(vocabularyLabel, termLabel);
			termRequestMap.put(vocabularyLabel, new Date());
		}

		ObjectResponse<VocabularyTerm> response = new ObjectResponse<>();
		response.setEntity(term);
		return response;
		
	}

	public ObjectResponse<VocabularyTerm> getTermInVocabularyTermSet(String vocabularyTermSetLabel, String termLabel) {

		VocabularyTerm term = null;
		
		if(termSetRequestMap.get(vocabularyTermSetLabel) != null) {
			HashMap<String, VocabularyTerm> termMap = vocabTermSetCacheMap.get(vocabularyTermSetLabel);
			
			if(termMap == null) {
				Log.debug("Vocab not cached, caching vocab: " + vocabularyTermSetLabel);
				termMap = new HashMap<>();
				vocabTermSetCacheMap.put(vocabularyTermSetLabel, termMap);
			}

			if(termMap.containsKey(termLabel)) {
				term = termMap.get(termLabel);
			} else {
				Log.debug("Term not cached, caching term: " + vocabularyTermSetLabel + "(" + termLabel + ")");
				term = getTermInVocabularyTermSetFromDB(vocabularyTermSetLabel, termLabel);
				if (term != null)
					term.getSynonyms().size();
				termMap.put(termLabel, term);
			}
		} else {
			term = getTermInVocabularyTermSetFromDB(vocabularyTermSetLabel, termLabel);
			termSetRequestMap.put(vocabularyTermSetLabel, new Date());
		}

		ObjectResponse<VocabularyTerm> response = new ObjectResponse<VocabularyTerm>();
		response.setEntity(term);
		return response;
		
	}
	
	private VocabularyTerm getTermInVocabularyFromDB(String vocabularyLabel, String termLabel) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", termLabel);
		params.put("vocabulary.vocabularyLabel", vocabularyLabel);
		SearchResponse<VocabularyTerm> resp = vocabularyTermDAO.findByParams(params);
		return resp.getSingleResult();
	}
	
	private VocabularyTerm getTermInVocabularyTermSetFromDB(String vocabularyTermSetLabel, String termLabel) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", termLabel);
		params.put("vocabularyTermSets.vocabularyLabel", vocabularyTermSetLabel);

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
