package org.alliancegenome.curation_api.services.ontology;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

@ApplicationScoped
public class EcoTermService extends BaseOntologyTermService<ECOTerm, EcoTermDAO> {
	
	@Inject EcoTermDAO ecoTermDAO;
	@Inject VocabularyDAO vocabularyDAO;
	
	private final String ecoTermAbbreviationVocabularyName = "AGR disease annotation ECO terms";
	private final String agrEcoTermSubset = "agr_eco_terms";
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(ecoTermDAO);
	}
	
	@Transactional
	public void updateAbbreviations() {

		SearchResponse<Vocabulary> res = vocabularyDAO.findByField("name", ecoTermAbbreviationVocabularyName);
		if(res != null && res.getTotalResults() == 1) {
			List<VocabularyTerm> ecoVocabularyTerms = res.getResults().get(0).getMemberTerms();
			ecoVocabularyTerms.forEach((ecoVocabularyTerm) -> {
				ECOTerm ecoTerm = ecoTermDAO.find(ecoVocabularyTerm.getName());
				if (ecoTerm != null) {
					ecoTerm.setAbbreviation(ecoVocabularyTerm.getAbbreviation());
					List<String> subsets = ecoTerm.getSubsets();
					if (!subsets.contains(agrEcoTermSubset)) {
						subsets.add(agrEcoTermSubset);
					}
					ecoTerm.setSubsets(subsets);
					ecoTermDAO.persist(ecoTerm);
				}
			});
		}
	}

}
