package org.alliancegenome.curation_api.services.ontology;

import java.util.List;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class EcoTermService extends BaseOntologyTermService<ECOTerm, EcoTermDAO> {

	@Inject
	EcoTermDAO ecoTermDAO;
	@Inject
	VocabularyDAO vocabularyDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(ecoTermDAO);
	}

	@Transactional
	public void updateAbbreviations() {

		SearchResponse<Vocabulary> res = vocabularyDAO.findByField("name", OntologyConstants.ECO_TERM_ABBREVIATION_VOCABULARY_NAME);
		if (res != null && res.getTotalResults() == 1) {
			List<VocabularyTerm> ecoVocabularyTerms = res.getResults().get(0).getMemberTerms();
			ecoVocabularyTerms.forEach((ecoVocabularyTerm) -> {
				ECOTerm ecoTerm = ecoTermDAO.find(ecoVocabularyTerm.getName());
				if (ecoTerm != null) {
					ecoTerm.setAbbreviation(ecoVocabularyTerm.getAbbreviation());
					List<String> subsets = ecoTerm.getSubsets();
					if (!subsets.contains(OntologyConstants.AGR_ECO_TERM_SUBSET)) {
						subsets.add(OntologyConstants.AGR_ECO_TERM_SUBSET);
					}
					ecoTerm.setSubsets(subsets);
					ecoTermDAO.persist(ecoTerm);
				}
			});
		}
	}

}
