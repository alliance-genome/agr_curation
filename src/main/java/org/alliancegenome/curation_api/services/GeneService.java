package org.alliancegenome.curation_api.services;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.base.SearchResults;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.entities.Gene;
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
import java.util.stream.Collectors;

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
            handleNewSynonyms(gene, g);
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
                handleUpdateSynonyms(gene, g);
                update(g);
            }
        }

    }

    private void handleNewSynonyms(GeneDTO gene, Gene g) {
        if (CollectionUtils.isNotEmpty(gene.getBasicGeneticEntity().getSynonyms())) {
            List<Synonym> synonyms = DtoConverter.getSynonyms(gene);
            synonyms.forEach(synonym -> {
                synonym.setGenomicEntityList(List.of(g));
                synonymService.create(synonym);
            });
            g.setSynonyms(synonyms);
        }
    }

    private void handleUpdateSynonyms(GeneDTO geneDTO, Gene gene) {
        if (CollectionUtils.isNotEmpty(geneDTO.getBasicGeneticEntity().getSynonyms())) {
            List<Synonym> newSynonyms = DtoConverter.getSynonyms(geneDTO);

            List<Synonym> existingSynonyms = gene.getSynonyms();

            // remove synonyms that are not found in the new synonym list
            if (CollectionUtils.isNotEmpty(existingSynonyms)) {
                List<String> existingSynonymStrings = existingSynonyms.stream().map(Synonym::getName).collect(Collectors.toList());
                List<Long> removeSynIDs = existingSynonyms.stream()
                        .filter(synonym -> !existingSynonymStrings.contains(synonym.getName()))
                        .map(Synonym::getId)
                        .collect(Collectors.toList());
                removeSynIDs.forEach(id -> synonymService.delete(Long.toString(id)));
                existingSynonyms.removeIf(synonym -> newSynonyms.stream().noneMatch(synonym1 -> synonym1.getName().equals(synonym.getName())));
            }
            // add new synonyms that are not found in the existing synonym list
            if (CollectionUtils.isNotEmpty(existingSynonyms)) {
                List<String> existingSynonymStrings = existingSynonyms.stream().map(Synonym::getName).collect(Collectors.toList());
                final List<Synonym> newCollect = newSynonyms.stream().filter(synonym -> !existingSynonymStrings.contains(synonym.getName())).collect(Collectors.toList());
                newCollect.forEach(synonym -> {
                    synonym.setGenomicEntityList(List.of(gene));
                    synonymService.create(synonym);
                });
                existingSynonyms.addAll(newCollect);
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
