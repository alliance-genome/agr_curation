package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.LiteratureReferenceDAO;
import org.alliancegenome.curation_api.model.document.LiteratureReference;
import org.alliancegenome.curation_api.services.base.BaseDocumentService;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class LiteratureReferenceService extends BaseDocumentService<LiteratureReference, LiteratureReferenceDAO> {
	
	@Inject LiteratureReferenceDAO literatureReferenceDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setESDao(literatureReferenceDAO);
	}
}
