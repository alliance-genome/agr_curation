package org.alliancegenome.curation_api.bulk.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.alliancegenome.curation_api.interfaces.bulk.MpTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.model.ingest.xml.dto.*;
import org.alliancegenome.curation_api.services.MpTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class MpTermBulkController implements MpTermBulkRESTInterface {
    
    @Inject MpTermService mpTermService;
    
    public Boolean updateMpTerms(UriInfo uriInfo, RDF rdf) {
        System.out.println("RDF Recieved:");

        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("MP Owl File Update", rdf.getClasses().length);
        int sum = 0;
        for(RDFClass c: rdf.getClasses()) {
            
            if(c.getId() != null) {
                MPTerm term = new MPTerm();
                term.setCurie(c.getId());
                term.setName(c.getLabel());
                term.setDefinition(c.getIAO_0000115());
                mpTermService.upsert(term);
                sum++;
            }

            if(c.getHasAlternativeId() != null && c.getHasAlternativeId().length > 0) {
                
                for(String term: c.getHasAlternativeId()) {

                    MPTerm mpTerm = new MPTerm();
                    mpTerm.setCurie(term);
                    //mpTerm.setName(c.getLabel());
                    mpTermService.upsert(mpTerm);
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
