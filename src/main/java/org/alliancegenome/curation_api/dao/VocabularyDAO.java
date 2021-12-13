package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Vocabulary;

@ApplicationScoped
public class VocabularyDAO extends BaseSQLDAO<Vocabulary> {

    protected VocabularyDAO() {
        super(Vocabulary.class);
    }
    
    public Long getIdFromName(String name) {
        Query jpqlQuery = entityManager.createQuery("SELECT vocabulary.id FROM Vocabulary vocabulary WHERE vocabulary.name=:name");
        jpqlQuery.setParameter("name", name);
        return(Long) jpqlQuery.getSingleResult();
    }
}
