package org.alliancegenome.curation_api.bulk.controllers;

import java.util.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.alliancegenome.curation_api.model.dto.xml.*;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.rest.interfaces.DoTermBulkInterface;
import org.alliancegenome.curation_api.services.DoTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DoTermBulkController implements DoTermBulkInterface {
	
	@Inject DoTermService doTermService;
	
	public Boolean updateDoTerms(UriInfo uriInfo, RDF rdf) {

		Map<String, Object> params = new HashMap<String, Object>();
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
		ph.startProcess("DO Owl File Update", rdf.getClasses().length);
		int sum = 0;
		for(RDFClass c: rdf.getClasses()) {
			
			if(c.getId() != null) {
				params.clear();
				params.put("curie", c.getId());
				List<DOTerm> terms = doTermService.search(params);
				
				if(terms.size() == 0) { // Insert
					DOTerm term = new DOTerm();
					term.setCurie(c.getId());
					term.setTermDefinition(c.getIAO_0000115());
					doTermService.create(term);
					sum++;
				} else if(terms.size() == 1) { // Update
					terms.get(0).setTermDefinition(c.getIAO_0000115());
					doTermService.update(terms.get(0));
					sum++;
				} else {
					log.debug("Error multipls terms found:" + c.getId());
				}
				
			}

			if(c.getHasAlternativeId() != null && c.getHasAlternativeId().length > 0) {
				
				for(String term: c.getHasAlternativeId()) {
					params.clear();
					params.put("curie", term);
					List<DOTerm> terms = doTermService.search(params);
					
					if(terms.size() == 0) { // Insert
						DOTerm doTerm = new DOTerm();
						doTerm.setCurie(term);
						doTermService.create(doTerm);
						sum++;
					}
				}
			}
			
			ph.progressProcess();
		}
		log.info("Total Terms: " + sum);
		
		ph.finishProcess();
		
		return true;
	}

}
