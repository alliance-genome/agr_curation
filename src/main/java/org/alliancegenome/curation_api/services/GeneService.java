package org.alliancegenome.curation_api.services;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.base.SearchResults;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneSynonym;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.json.dto.GeneDTO;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Session;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;

@JBossLog
@ApplicationScoped
public class GeneService extends BaseService<Gene, GeneDAO> implements Runnable {

    @Inject
    GeneDAO geneDAO;

    @Inject
    SynonymService synonymService;

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
        if (gene != null) {
            gene.getSynonyms().size();
            gene.getSecondaryIdentifiers().size();
        }
        return gene;
    }

    @Transactional
    public void processUpdate(GeneDTO gene) {

        Map<String, Object> params = new HashMap<>();

        params.put("curie", gene.getBasicGeneticEntity().getPrimaryId());
        List<Gene> genes = findByParams(params);
        if (genes == null || genes.size() == 0) {
            Gene g = new Gene();
            g.setGeneSynopsis(gene.getGeneSynopsis());
            g.setGeneSynopsisURL(gene.getGeneSynopsisUrl());
            g.setCurie(gene.getBasicGeneticEntity().getPrimaryId());
            g.setSymbol(gene.getSymbol());
            g.setName(gene.getName());
            g.setTaxon(gene.getBasicGeneticEntity().getTaxonId());
            g.setType(gene.getSoTermId());
            create(g);
            if (CollectionUtils.isNotEmpty(gene.getBasicGeneticEntity().getSynonyms())) {
                List<GeneSynonym> synonyms = gene.getBasicGeneticEntity().getSynonyms().stream()
                        .map(s -> {
                            final GeneSynonym geneSynonym = new GeneSynonym(s);
                            geneSynonym.setGene(g);
                            return geneSynonym;
                        }).collect(toList());
                synonyms.forEach(synonym -> synonymService.create(synonym));
                g.setSynonyms(synonyms);
            }
            //producer.send(queue, g);
        } else {
            Gene g = genes.get(0);
            if (g.getCurie().equals(gene.getBasicGeneticEntity().getPrimaryId())) {
                g.setGeneSynopsis(gene.getGeneSynopsis());
                g.setGeneSynopsisURL(gene.getGeneSynopsisUrl());
                g.setCurie(gene.getBasicGeneticEntity().getPrimaryId());
                g.setSymbol(gene.getSymbol());
                g.setName(gene.getName());
                g.setTaxon(gene.getBasicGeneticEntity().getTaxonId());
                g.setType(gene.getSoTermId());
                //producer.send(queue, g);
                update(g);
            }
        }

    }


    @Inject
    ConnectionFactory connectionFactory;

    private int threadCount = 3;

    private final ExecutorService scheduler = Executors.newFixedThreadPool(threadCount);

    void onStart(@Observes StartupEvent ev) {
        log.info("GeneService Queue Starting:");
        for (int i = 0; i < threadCount; i++) {
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
