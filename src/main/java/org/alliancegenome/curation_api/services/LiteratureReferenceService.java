package org.alliancegenome.curation_api.services;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.LiteratureReferenceDAO;
import org.alliancegenome.curation_api.model.document.LiteratureReference;
import org.alliancegenome.curation_api.services.base.BaseDocumentService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class LiteratureReferenceService extends BaseDocumentService<LiteratureReference, LiteratureReferenceDAO> {

	@Inject
	LiteratureReferenceDAO literatureReferenceDAO;

	@Override
	@PostConstruct
	protected void init() {
		setESDao(literatureReferenceDAO);
	}
}
