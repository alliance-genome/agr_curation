package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.model.entities.Synonym;

@ApplicationScoped
public class SynonymService extends BaseCrudService<Synonym, SynonymDAO> {

    @Inject
    SynonymDAO synonymDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(synonymDAO);
    }

}
