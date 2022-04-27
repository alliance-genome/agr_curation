package org.alliancegenome.curation_api.services;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.ingest.fms.dto.CrossReferenceFmsDTO;

@RequestScoped
public class CrossReferenceService extends BaseCrudService<CrossReference, CrossReferenceDAO>{

    @Inject CrossReferenceDAO crossReferenceDAO;


    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(crossReferenceDAO);
    }

    @Transactional
    public CrossReference processUpdate(CrossReferenceFmsDTO crossReferenceFmsDTO) {

        CrossReference crossReference = crossReferenceDAO.find(crossReferenceFmsDTO.getId());
        if(crossReference == null) {
            crossReference = new CrossReference();
            crossReference.setCurie(crossReferenceFmsDTO.getId());
            crossReference.setInternal(false);
            crossReferenceDAO.persist(crossReference);
        }

        handlePageAreas(crossReferenceFmsDTO, crossReference);

        return crossReference;

    }

    private void handlePageAreas(CrossReferenceFmsDTO crDTO, CrossReference cr) {

        Set<String> currentPages;
        if(cr.getPageAreas() == null) {
            currentPages = new HashSet<>();
            cr.setPageAreas(new ArrayList<>());
        } else {
            currentPages = cr.getPageAreas().stream().collect(Collectors.toSet());
        }

        Set<String> newPages;
        if(crDTO.getPages() == null) {
            newPages = new HashSet<>();
        } else {
            newPages = crDTO.getPages().stream().collect(Collectors.toSet());
        }

        newPages.forEach(id -> {
            if(!currentPages.contains(id)) {
                cr.getPageAreas().add(id);
            }
        });

        currentPages.forEach(id -> {
            if(!newPages.contains(id)) {
                cr.getPageAreas().remove(id);
            }
        });

    }

}
