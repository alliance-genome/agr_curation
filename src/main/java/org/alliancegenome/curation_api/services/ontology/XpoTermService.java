package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.XpoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XPOTerm;

@RequestScoped
public class XpoTermService extends BaseOntologyTermService<XPOTerm, XpoTermDAO> {

    @Inject XpoTermDAO xpoTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(xpoTermDAO);
    }

}
