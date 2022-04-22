package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.XsmoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XSMOTerm;

@RequestScoped
public class XsmoTermService extends BaseOntologyTermService<XSMOTerm, XsmoTermDAO> {

    @Inject XsmoTermDAO xsmoTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(xsmoTermDAO);
    }

}
