package org.alliancegenome.curation_api.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.dao.SystemSQLDAO;
import org.alliancegenome.curation_api.interfaces.SystemControllerInterface;

@RequestScoped
public class SystemController implements SystemControllerInterface {

    @Inject SystemSQLDAO systemSQLDAO;
    
    @Override
    public void reindexEverything(Integer threads, Integer indexAmount, Integer batchSize) {
        systemSQLDAO.reindexEverything(threads, indexAmount, batchSize);
    }
    
}
