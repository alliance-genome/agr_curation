package org.alliancegenome.curation_api.bulk.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.*;

import org.alliancegenome.curation_api.interfaces.bulk.GeneBulkRESTInterface;
import org.alliancegenome.curation_api.model.dto.json.*;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneBulkController implements GeneBulkRESTInterface {

	@Inject
	ConnectionFactory connectionFactory;

	@Override
	public String updateBGI(GeneMetaDataDTO geneData) {

		try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
			ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
			ph.startProcess("Gene Update", geneData.getData().size());
			for(GeneDTO gene: geneData.getData()) {
				context.createProducer().send(context.createQueue("geneQueue"), context.createObjectMessage(gene));
				ph.progressProcess();
			}
			ph.finishProcess();
		}

		return "OK";
	}

}
