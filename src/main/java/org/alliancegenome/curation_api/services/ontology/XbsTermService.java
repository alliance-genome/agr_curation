package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.XbsTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XBSTerm;

@RequestScoped
public class XbsTermService extends BaseOntologyTermService<XBSTerm, XbsTermDAO> {

    @Inject XbsTermDAO xbsTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(xbsTermDAO);
    }

}
