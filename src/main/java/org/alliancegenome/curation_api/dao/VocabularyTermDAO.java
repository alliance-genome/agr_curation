package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VocabularyTermDAO extends BaseSQLDAO<VocabularyTerm> {

	protected VocabularyTermDAO() {
		super(VocabularyTerm.class);
	}

}
