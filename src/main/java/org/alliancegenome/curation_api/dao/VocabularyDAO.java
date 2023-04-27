package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Vocabulary;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VocabularyDAO extends BaseSQLDAO<Vocabulary> {

	protected VocabularyDAO() {
		super(Vocabulary.class);
	}

}
