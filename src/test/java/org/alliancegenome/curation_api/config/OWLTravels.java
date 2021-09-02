package org.alliancegenome.curation_api.config;

import java.io.File;
import java.util.*;
import java.util.stream.*;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import lombok.extern.java.Log;


@Log
public class OWLTravels {

    private OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    private OWLDataFactory df = OWLManager.getOWLDataFactory();
    private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private OWLReasoner reasoner;
    private OWLOntology ontology;
    
    public static void main(String[] args) throws Exception {
        new OWLTravels();
    }

    public OWLTravels() throws Exception {


        //IRI pizza_iri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
        IRI pizza_iri = IRI.create(new File("/Users/olinblodgett/Desktop/FMS/doid.owl"));

        ontology = manager.loadOntologyFromOntologyDocument(pizza_iri);
        // Create the walker
        //OWLReasoner reasoner = OWLReasonerFactory.createReasoner(o);

        reasoner = reasonerFactory.createReasoner(ontology);

        OWLClass root = manager.getOWLDataFactory().getOWLThing();

        System.out.println("Ontology Loaded...");
        System.out.println("Document IRI: " + pizza_iri);
        System.out.println("Ontology : " + ontology.getOntologyID());
        System.out.println("Format      : " + manager.getOntologyFormat(ontology));


        traverse(root, 0);

    }

    public void traverse(OWLClass parent, int depth) {

        if (reasoner.isSatisfiable(parent)) {

            for(int i = 0; i < depth; i++) {
                System.out.print("   ");
            }

            System.out.println(getLabel(parent));

            //.map(e-> {
            //  System.out.println(e.toStringID());
            //});

            for (OWLClass child : reasoner.getSubClasses(parent, true).getFlattened()) {
                if (!child.equals(parent)) {
                    traverse(child, depth + 1);
                }
            }
        }

    }

    public String getLabel(OWLClass node) {

        //EntitySearcher.getAnnotationObjects(node, ontology).forEach(e -> { System.out.println(e); });
        
        List<String> ret = EntitySearcher.getAnnotationObjects(node, ontology).map(e -> {

            System.out.println("Val: " + e.getValue());
            System.out.println("Prop: " + e.getProperty());
            if(e.getProperty().isLabel()) {
                return ((OWLLiteral)e.getValue()).getLiteral();
            } else {
                return null;
            }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

        if(ret.size() == 0) return node.toString();
        else return ret.get(0);
    }




}
