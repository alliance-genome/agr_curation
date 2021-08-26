package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.*;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.ingest.json.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.model.input.Pagination;

import lombok.extern.jbosslog.JBossLog;


@JBossLog
@RequestScoped
public class CrossReferenceService extends BaseService<CrossReference, CrossReferenceDAO>{

    @Inject CrossReferenceDAO crossReferenceDAO;


    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(crossReferenceDAO);
    }

    public SearchResults<CrossReference> getAllCrossReferences(Pagination pagination) {
        return getAll(pagination);
    }

    @Transactional
    public CrossReference processUpdate(CrossReferenceDTO crossReferenceDTO) {

        CrossReference crossReference = crossReferenceDAO.find(crossReferenceDTO.getId());
        if(crossReference == null) {
            crossReference = new CrossReference();
            crossReference.setCurie(crossReferenceDTO.getId());
            crossReference.setPageAreas(crossReferenceDTO.getPages());
            create(crossReference);
        } else {
            if(crossReference.getCurie().equals(crossReferenceDTO.getId())) {
                crossReference.setPageAreas(crossReferenceDTO.getPages());
                update(crossReference);
            }
        }

        return crossReference;

    }
}
