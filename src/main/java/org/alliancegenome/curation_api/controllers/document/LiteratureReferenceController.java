package org.alliancegenome.curation_api.controllers.document;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseDocumentController;
import org.alliancegenome.curation_api.dao.LiteratureReferenceDAO;
import org.alliancegenome.curation_api.interfaces.document.LiteratureReferenceInterface;
import org.alliancegenome.curation_api.model.document.LiteratureReference;
import org.alliancegenome.curation_api.services.LiteratureReferenceService;

import io.quarkus.logging.Log;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class LiteratureReferenceController extends BaseDocumentController<LiteratureReferenceService, LiteratureReference, LiteratureReferenceDAO> implements LiteratureReferenceInterface {

    @Inject LiteratureReferenceService literatureReferenceService;

    @Override
    @PostConstruct
    protected void init() {
        setService(literatureReferenceService);
    }

}
