package org.alliancegenome.curation_api.services.helpers.vocabularyTerms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.collections4.CollectionUtils;

@RequestScoped
public class VocabularyTermValidationHelper {
	
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	
	public Map<String, Map<String, VocabularyTerm>> constructTermValidationMap(List<String> vocabularies, List<String> termSets) {
		
		Map<String, Map<String,VocabularyTerm>> validationMap = new HashMap<>();
		
		for (String vocabularyName : vocabularies) {
			Map<String, VocabularyTerm> termsInVocab = new HashMap<>();
			SearchResponse<VocabularyTerm> response = vocabularyTermDAO.findByField("vocabulary.name", vocabularyName);	
			if (response != null && CollectionUtils.isNotEmpty(response.getResults())) {
				for (VocabularyTerm term : response.getResults()) {
					termsInVocab.put(term.getName(), term);
				}
			}
			validationMap.put(vocabularyName, termsInVocab);
		}
		
		for (String termSet : termSets) {
			Map<String, VocabularyTerm> termsInSet = new HashMap<>();
			SearchResponse<VocabularyTerm> response = vocabularyTermDAO.findByField("vocabularyTermSets.name", termSet);	
			if (response != null && CollectionUtils.isNotEmpty(response.getResults())) {
				for (VocabularyTerm term : response.getResults()) {
					termsInSet.put(term.getName(), term);
				}
			}
			validationMap.put(termSet, termsInSet);
		}
		
		return validationMap;
	}
}
