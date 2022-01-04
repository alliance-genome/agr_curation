package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.ZfaTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZfaTerm;

@RequestScoped
public class ZfaTermService extends BaseOntologyTermService<ZfaTerm, ZfaTermDAO> {

    @Inject
    ZfaTermDAO zfaTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(zfaTermDAO);
    }

}
