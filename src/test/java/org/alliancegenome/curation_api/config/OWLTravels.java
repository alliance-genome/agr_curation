package org.alliancegenome.curation_api.config;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.rdf.rdfxml.renderer.OWLOntologyXMLNamespaceManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

public class OWLTravels implements OWLObjectVisitor {

    private OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    //private OWLDataFactory df = OWLManager.getOWLDataFactory();
    private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private OWLReasoner reasoner;
    private OWLOntology ontology;
    
    public static void main(String[] args) throws Exception {
        new OWLTravels();
    }

    public OWLTravels() throws Exception {


        //IRI pizza_iri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
        IRI do_iri = IRI.create(new File("/Users/balrog/git/agr_curation/doid.owl"));

        ontology = manager.loadOntologyFromOntologyDocument(do_iri);

        reasoner = reasonerFactory.createReasoner(ontology);

        OWLClass root = manager.getOWLDataFactory().getOWLThing();

        System.out.println("Ontology Loaded...");
        System.out.println("Document IRI: " + do_iri);
        System.out.println("Ontology : " + ontology.getOntologyID());
        System.out.println("Format      : " + manager.getOntologyFormat(ontology));

        OWLDocumentFormat format = manager.getOntologyFormat(ontology);
        
        OWLOntologyXMLNamespaceManager nsManager = new OWLOntologyXMLNamespaceManager(ontology, format);
        System.out.println(nsManager.getDefaultNamespace());
        
        for (String prefix : nsManager.getPrefixes()) {
            System.out.println(prefix);
        }
        for (String ns : nsManager.getNamespaces()) {
            System.out.println(ns);
        }
        
        
        traverse(root, 0);

    }

    public void traverse(OWLClass parent, int depth) {

        if (reasoner.isSatisfiable(parent)) {

            //System.out.println("--------------------------------------------------------------------------");
            System.out.println(getLabel(parent, depth));
            //System.out.println("--------------------------------------------------------------------------");

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

    public String getLabel(OWLClass node, int depth) {

        //EntitySearcher.getAnnotationObjects(node, ontology).forEach(e -> { System.out.println(e); });
        
        node.accept(this);
        
        List<String> ret = EntitySearcher.getAnnotationObjects(node, ontology).map(e -> {

            for(int i = 0; i < depth; i++) {
                System.out.print("   ");
            }
            //System.out.println("Val: " + e.getValue());
            System.out.println("Prop: " + e.getProperty().getIRI());
            //System.out.println("Sig: " + e);
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

    @Override
    public void visit(OWLDeclarationAxiom axiom) {
        //System.out.println(axiom);
        OWLObjectVisitor.super.visit(axiom);
    }

    @Override
    public void visit(OWLDatatypeDefinitionAxiom axiom) {
        //System.out.println(axiom);
        OWLObjectVisitor.super.visit(axiom);
    }


}
