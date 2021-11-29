package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;

@ApplicationScoped
public class VocabularyTermDAO extends BaseSQLDAO<VocabularyTerm> {

    protected VocabularyTermDAO() {
        super(VocabularyTerm.class);
    }
}
