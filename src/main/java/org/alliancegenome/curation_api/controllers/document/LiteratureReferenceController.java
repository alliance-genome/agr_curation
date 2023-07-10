package org.alliancegenome.curation_api.controllers.document;

import org.alliancegenome.curation_api.controllers.base.BaseDocumentController;
import org.alliancegenome.curation_api.dao.LiteratureReferenceDAO;
import org.alliancegenome.curation_api.interfaces.document.LiteratureReferenceInterface;
import org.alliancegenome.curation_api.model.document.LiteratureReference;
import org.alliancegenome.curation_api.services.LiteratureReferenceService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class LiteratureReferenceController extends BaseDocumentController<LiteratureReferenceService, LiteratureReference, LiteratureReferenceDAO> implements LiteratureReferenceInterface {

	@Inject
	LiteratureReferenceService literatureReferenceService;

	@Override
	@PostConstruct
	protected void init() {
		setService(literatureReferenceService);
	}

}
