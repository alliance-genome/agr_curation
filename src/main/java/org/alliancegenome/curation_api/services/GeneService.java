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
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.dto.Pagination;
import org.alliancegenome.curation_api.model.dto.json.GeneDTO;
import org.alliancegenome.curation_api.model.entities.Gene;

import io.quarkus.runtime.*;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class GeneService extends BaseService<Gene, GeneDAO> implements Runnable {

	@Inject GeneDAO geneDAO;

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

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("curie", gene.getBasicGeneticEntity().getPrimaryId());
		List<Gene> genes = findByParams(params);
		if(genes == null || genes.size() == 0) {
			Gene g = new Gene();
			g.setGeneSynopsis(gene.getGeneSynopsis());
			g.setGeneSynopsisURL(gene.getGeneSynopsisUrl());
			g.setCurie(gene.getBasicGeneticEntity().getPrimaryId());
			g.setSymbol(gene.getSymbol());
			g.setName(gene.getName());
			g.setTaxon(gene.getBasicGeneticEntity().getTaxonId());
			g.setType(gene.getSoTermId());
			//producer.send(queue, g);
			create(g);
		} else {
			Gene g = genes.get(0);
			if(g.getCurie().equals(gene.getBasicGeneticEntity().getPrimaryId())) {
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
	
	private int threadCount = 5;

	private final ExecutorService scheduler = Executors.newFixedThreadPool(threadCount);

	void onStart(@Observes StartupEvent ev) {
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
		}

	}


}
