package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.ZfsTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZFSTerm;

@RequestScoped
public class ZfsTermService extends BaseOntologyTermService<ZFSTerm, ZfsTermDAO> {

    @Inject
    ZfsTermDAO zfsTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(zfsTermDAO);
    }

}
