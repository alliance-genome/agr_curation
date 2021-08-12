package org.alliancegenome.curation_api.services;

import java.util.*;
import java.util.concurrent.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.*;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.model.dto.Pagination;
import org.alliancegenome.curation_api.model.dto.json.*;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import io.quarkus.runtime.*;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleService extends BaseService<Allele, AlleleDAO> implements Runnable {

	@Inject AlleleDAO alleleDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDAO);
	}

	public SearchResults<Allele> getAllAlleles(Pagination pagination) {
		return getAll(pagination);
	}

	@Transactional
	public void processUpdate(AlleleDTO allele) {

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("curie", allele.getPrimaryId());
		Allele dbAllele = get(allele.getPrimaryId());
		if(dbAllele == null) {
			dbAllele = new Allele();
			dbAllele.setCurie(allele.getPrimaryId());
			dbAllele.setSymbol(allele.getSymbol());
			dbAllele.setDescription(allele.getDescription());
			dbAllele.setTaxon(allele.getTaxonId());
			create(dbAllele);
		} else {
			if(dbAllele.getCurie().equals(allele.getPrimaryId())) {
				dbAllele.setSymbol(allele.getSymbol());
				dbAllele.setDescription(allele.getDescription());
				dbAllele.setTaxon(allele.getTaxonId());
				update(dbAllele);
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
			JMSConsumer consumer = context.createConsumer(context.createQueue("alleleQueue"));
			while (true) {
				processUpdate(consumer.receiveBody(AlleleDTO.class));
			}
		}

	}


}
