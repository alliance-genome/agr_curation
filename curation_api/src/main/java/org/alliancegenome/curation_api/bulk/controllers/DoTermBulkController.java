package org.alliancegenome.curation_api.bulk.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.alliancegenome.curation_api.interfaces.bulk.DoTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.dto.xml.*;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.services.DoTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DoTermBulkController implements DoTermBulkRESTInterface {
	
	@Inject DoTermService doTermService;
	
	public Boolean updateDoTerms(UriInfo uriInfo, RDF rdf) {


		ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
		ph.startProcess("DO Owl File Update", rdf.getClasses().length);
		int sum = 0;
		for(RDFClass c: rdf.getClasses()) {
			
			if(c.getId() != null) {
				DOTerm term = new DOTerm();
				term.setCurie(c.getId());
				term.setName(c.getLabel());
				term.setDefinition(c.getIAO_0000115());
				doTermService.upsert(term);
				sum++;
			}

			if(c.getHasAlternativeId() != null && c.getHasAlternativeId().length > 0) {
				
				for(String term: c.getHasAlternativeId()) {

					DOTerm doTerm = new DOTerm();
					doTerm.setCurie(term);
					//doTerm.setName(c.getLabel());
					doTermService.upsert(doTerm);
					sum++;
				}
			}
			
			ph.progressProcess();
		}
		log.info("Total Terms: " + sum);
		
		ph.finishProcess();
		
		return true;
	}

}
