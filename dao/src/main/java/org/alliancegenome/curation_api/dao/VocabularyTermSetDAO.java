package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.response.SearchResponse;

@ApplicationScoped
public class VocabularyTermSetDAO extends BaseSQLDAO<VocabularyTermSet> {
	
	protected VocabularyTermSetDAO() {
		super(VocabularyTermSet.class);
	}
}
