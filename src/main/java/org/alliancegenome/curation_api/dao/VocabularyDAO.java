package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Vocabulary;

@ApplicationScoped
public class VocabularyDAO extends BaseSQLDAO<Vocabulary> {

	protected VocabularyDAO() {
		super(Vocabulary.class);
	}

}
