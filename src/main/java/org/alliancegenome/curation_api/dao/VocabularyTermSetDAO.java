package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VocabularyTermSetDAO extends BaseSQLDAO<VocabularyTermSet> {

	protected VocabularyTermSetDAO() {
		super(VocabularyTermSet.class);
	}
}
