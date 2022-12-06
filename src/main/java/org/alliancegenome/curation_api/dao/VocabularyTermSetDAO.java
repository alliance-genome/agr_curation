package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;

@ApplicationScoped
public class VocabularyTermSetDAO extends BaseSQLDAO<VocabularyTermSet> {

	protected VocabularyTermSetDAO() {
		super(VocabularyTermSet.class);
	}
}
