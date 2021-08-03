package org.alliancegenome.curation_api.bulk.controllers;

import java.util.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.interfaces.bulk.GeneBulkRESTInterface;
import org.alliancegenome.curation_api.model.dto.json.*;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;


//@JMSDestinationDefinitions(
//	    value = {
//	        @JMSDestinationDefinition(
//	            name = "java:jboss/exported/jms/queue/bgiprocessing",
//	            interfaceName = "javax.jms.Queue",
//	            destinationName = "bgiprocessing"
//	        )
//	    }
//	)



@JBossLog
@RequestScoped
public class GeneBulkController implements GeneBulkRESTInterface {

	@Inject GeneService geneSerice;
	
//	@Resource(lookup = "java:jboss/exported/jms/queue/bgiprocessing")
//    private Queue queue;
	
	@Override
	public String updateBGI(GeneMetaDataDTO geneData) {

		Map<String, Object> params = new HashMap<String, Object>();
		ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
		ph.startProcess("BGI Gene Update", geneData.getData().size());
		for(GeneDTO gene: geneData.getData()) {
			params.put("curie", gene.getBasicGeneticEntity().getPrimaryId());
			List<Gene> genes = geneSerice.findByParams(params);
			if(genes == null || genes.size() == 0) {
				Gene g = new Gene();
				g.setGeneSynopsis(gene.getGeneSynopsis());
				g.setGeneSynopsisURL(gene.getGeneSynopsisUrl());
				g.setCurie(gene.getBasicGeneticEntity().getPrimaryId());
				g.setSymbol(gene.getSymbol());
				g.setName(gene.getName());
				g.setTaxon(gene.getBasicGeneticEntity().getTaxonId());
				g.setType(gene.getSoTermId());
				geneSerice.create(g);
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
					geneSerice.update(g);
				}
			}
			
			ph.progressProcess();
		}
		ph.finishProcess();
		return "OK";

	}

}
