package org.alliancegenome.curation_api.services;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.base.SearchResults;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.dto.Pagination;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Reference;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@JBossLog
@RequestScoped
public class ReferenceService extends BaseService<Reference, ReferenceDAO> {

	@Inject
	ReferenceDAO referenceDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(referenceDAO);
	}
	
}
