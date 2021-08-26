package org.alliancegenome.curation_api.services;

import java.util.concurrent.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.DoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;

import io.quarkus.runtime.*;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DoTermService extends BaseService<DOTerm, DoTermDAO> {

    @Inject DoTermDAO doTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(doTermDAO);
    }

    @Transactional
    public DOTerm processUpdate(DOTerm dto) {

        DOTerm term = doTermDAO.find(dto.getCurie());

        if(term == null) {
            term = new DOTerm();
            term.setCurie(dto.getCurie());
            term.setObsolete(dto.getObsolete());
            term.setNamespace(dto.getNamespace());
            term.setName(dto.getName());
            term.setDefinition(dto.getDefinition());
            doTermDAO.persist(term);
        } else {
            term.setObsolete(dto.getObsolete());
            term.setNamespace(dto.getNamespace());
            term.setName(dto.getName());
            term.setDefinition(dto.getDefinition());
            doTermDAO.merge(term);
        }

        return term;

    }
    
}
