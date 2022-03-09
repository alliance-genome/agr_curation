package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.XaoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XAOTerm;

@RequestScoped
public class XaoTermService extends BaseOntologyTermService<XAOTerm, XaoTermDAO> {

    @Inject XaoTermDAO xaoTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(xaoTermDAO);
    }

}
