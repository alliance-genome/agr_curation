package org.alliancegenome.curation_api.services;

import java.util.*;
import java.util.concurrent.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.*;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.json.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.GeneDTO;
import org.alliancegenome.curation_api.model.input.Pagination;

import io.quarkus.runtime.*;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class GeneService extends BaseService<Gene, GeneDAO> implements Runnable {

    @Inject GeneDAO geneDAO;
    @Inject CrossReferenceDAO crossReferenceDAO;
    @Inject CrossReferenceService crossReferenceService;


    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(geneDAO);
    }

    public SearchResults<Gene> getAllGenes(Pagination pagination) {
        return getAll(pagination);
    }

    @Transactional
    public Gene getByIdOrCurie(String id) {
        Gene gene = geneDAO.getByIdOrCurie(id);
        if(gene != null) {
            gene.getSynonyms().size();
            gene.getSecondaryIdentifiers().size();
        }
        return gene;
    }

    @Transactional
    public void processUpdate(GeneDTO gene) {

        Gene g = geneDAO.find(gene.getBasicGeneticEntity().getPrimaryId());
        boolean newGene = false;

        if(g == null ) {
            g = new Gene();
            g.setCurie(gene.getBasicGeneticEntity().getPrimaryId());
            newGene = true;
        }

        g.setGeneSynopsis(gene.getGeneSynopsis());
        g.setGeneSynopsisURL(gene.getGeneSynopsisUrl());

        g.setSymbol(gene.getSymbol());
        g.setName(gene.getName());
        g.setTaxon(gene.getBasicGeneticEntity().getTaxonId());
        g.setType(gene.getSoTermId());

        List<CrossReferenceDTO> incomingCrossReferences = gene.getBasicGeneticEntity().getCrossReferences();
        List<CrossReference> persitentCrossReferences = new ArrayList<>();
        for(CrossReferenceDTO crossReferenceDTO : incomingCrossReferences){
            CrossReference crossReference = crossReferenceService.processUpdate(crossReferenceDTO);
            persitentCrossReferences.add(crossReference);
        }

        g.setCrossReferences(persitentCrossReferences);

        if(newGene) {
            create(g);
        }else {
            update(g);
        }

    }


    @Inject
    ConnectionFactory connectionFactory;
    
    private int threadCount = 3;

    private final ExecutorService scheduler = Executors.newFixedThreadPool(threadCount);

    void onStart(@Observes StartupEvent ev) {
        log.info("GeneService Queue Starting:");
        for(int i = 0; i < threadCount; i++) {
            scheduler.submit(new Thread(this));
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
    }

    @Override
    public void run() {
        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            JMSConsumer consumer = context.createConsumer(context.createQueue("geneQueue"));
            while (true) {
                processUpdate(consumer.receiveBody(GeneDTO.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
