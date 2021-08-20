package org.alliancegenome.curation_api.bulk.controllers;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.*;
import javax.ws.rs.core.UriInfo;

import org.alliancegenome.curation_api.interfaces.bulk.DoTermBulkRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.ingest.xml.dto.*;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DoTermBulkController implements DoTermBulkRESTInterface {
    
    @Inject
    ConnectionFactory connectionFactory;

    @Override
    public Boolean updateDoTerms(UriInfo uriInfo, RDF rdf) {

        try (JMSContext context = connectionFactory.createContext(Session.AUTO_ACKNOWLEDGE)) {
            ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
            ph.startProcess("DoTerm Update", rdf.getClasses().length);
            
            String nameSpace = rdf.getOntology().getDefaultNamespace();
            
            for(RDFClass c: rdf.getClasses()) {
                
                if(c.getId() != null) {
                    DOTerm term = new DOTerm();
                    if(c.getDeprecated() != null) {
                        term.setObsolete(c.getDeprecated().equals("true"));
                    }
                    term.setNamespace(nameSpace);
                    term.setCurie(c.getId());
                    term.setName(c.getLabel());
                    term.setDefinition(c.getIAO_0000115());
                    context.createProducer().send(context.createQueue("doTermQueue"), context.createObjectMessage(term));
                }

                if(c.getHasAlternativeId() != null && c.getHasAlternativeId().length > 0) {
                    
                    for(String term: c.getHasAlternativeId()) {

                        DOTerm doTerm = new DOTerm();
                        doTerm.setCurie(term);
                        //doTerm.setName(c.getLabel());
                        context.createProducer().send(context.createQueue("doTermQueue"), context.createObjectMessage(doTerm));

                    }
                }
                ph.progressProcess();
            }

            ph.finishProcess();
        }

        return true;
    }

}
