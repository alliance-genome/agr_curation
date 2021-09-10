package org.alliancegenome.curation_api.config;

import java.io.File;
import java.util.Map;

import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoader;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class OWLTravels {



    public static void main(String[] args) throws Exception {
        
        
        //IRI do_iri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
        //IRI do_iri = IRI.create(new File("/Users/balrog/git/agr_curation/doid.owl"));
        //IRI term_iri = IRI.create(fullText);
        //IRI do_iri = IRI.create();

        
        GenericOntologyLoader<EcoTerm> loader = new GenericOntologyLoader<EcoTerm>(EcoTerm.class);
        
        Map<String, EcoTerm> list = loader.load(new File("/Users/olinblodgett/Desktop/FMS/eco.owl"));
        
    }

    

}
