package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.interfaces.crud.GenomicEntityCrudInterface;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.services.GenomicEntityService;

@RequestScoped
public class GenomicEntityCrudController extends BaseCrudController<GenomicEntityService, GenomicEntity, GenomicEntityDAO> implements GenomicEntityCrudInterface {

    @Inject GenomicEntityService genomicEntityService;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(genomicEntityService);
    }

}
