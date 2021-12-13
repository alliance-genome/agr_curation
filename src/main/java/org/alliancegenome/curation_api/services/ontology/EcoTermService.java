package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.services.VocabularyService;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;

@RequestScoped
public class EcoTermService extends BaseOntologyTermService<EcoTerm, EcoTermDAO> {
    
    @Inject EcoTermDAO ecoTermDAO;
    @Inject VocabularyService vocabularyService;
    
    private final String ecoTermAbbreviationVocabularyName = "Disease annotation evidence code abbreviations";
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(ecoTermDAO);
    }
    
    @Transactional
    public void updateAbbreviations() {
        Vocabulary vocabulary = vocabularyService.getByName(ecoTermAbbreviationVocabularyName);
        List<VocabularyTerm> ecoVocabularyTerms = vocabulary.getMemberTerms();
        ecoVocabularyTerms.forEach((ecoVocabularyTerm) -> {
            EcoTerm ecoTerm = ecoTermDAO.find(ecoVocabularyTerm.getName());
            if (ecoTerm != null) {
                ecoTerm.setAbbreviation(ecoVocabularyTerm.getAbbreviation());
                ecoTermDAO.persist(ecoTerm);
            }
        });
    }
}
