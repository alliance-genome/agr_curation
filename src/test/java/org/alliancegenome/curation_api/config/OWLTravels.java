package org.alliancegenome.curation_api.config;

import java.io.File;
import java.util.ArrayList;

import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

public class OWLTravels<T extends OntologyTerm> implements OWLObjectVisitor {

    private OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    //private OWLDataFactory df = OWLManager.getOWLDataFactory();
    private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private OWLReasoner reasoner;
    private OWLOntology ontology;
    
    private String defaultNamespace;
    private Class<T> clazz;
    
    public static void main(String[] args) throws Exception {
        new OWLTravels<DOTerm>(DOTerm.class);
    }

    public OWLTravels(Class<T> clazz) throws Exception {
        this.clazz = clazz;

        //IRI do_iri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
        //IRI do_iri = IRI.create(new File("/Users/balrog/git/agr_curation/doid.owl"));
        IRI do_iri = IRI.create(new File("/Users/olinblodgett/Desktop/FMS/doid.owl"));
        
        ontology = manager.loadOntologyFromOntologyDocument(do_iri);

        ontology.annotations().forEach(a -> {
            String key = a.getProperty().getIRI().getShortForm();
            System.out.println(key);
            System.out.println(a.getValue());
            if(key.equals("default-namespace")) {
                defaultNamespace = getString(a.getValue());
            }
        });
        
        OWLClass root = manager.getOWLDataFactory().getOWLThing();

        System.out.println("Ontology Loaded...");
        System.out.println("Document IRI: " + do_iri);
        System.out.println("Ontology : " + ontology.getOntologyID());
        System.out.println("Format      : " + manager.getOntologyFormat(ontology));

//      OWLDocumentFormat format = manager.getOntologyFormat(ontology);
//      
//      OWLOntologyXMLNamespaceManager nsManager = new OWLOntologyXMLNamespaceManager(ontology, format);
//      System.out.println(nsManager.getDefaultNamespace());
//      
//      for (String prefix : nsManager.getPrefixes()) {
//          System.out.println(prefix);
//      }
//      for (String ns : nsManager.getNamespaces()) {
//          System.out.println(ns);
//      }

        reasoner = reasonerFactory.createReasoner(ontology);
        
        traverse(root, 0);

    }

    public void traverse(OWLClass parent, int depth) throws Exception {

        if (reasoner.isSatisfiable(parent)) {

            //System.out.println("--------------------------------------------------------------------------");

            T t = getOntologyTerm(parent);
            
            //System.out.println("--------------------------------------------------------------------------");
            //System.out.println(defaultNamespace);
            
            if((t.getNamespace() != null && t.getNamespace().equals(defaultNamespace)) || depth == 0) {
                System.out.println(t);
                reasoner.getSubClasses(parent, true).entities().forEach(child -> {
                    if (!child.equals(parent)) {
                        try {
                            traverse(child, depth + 1);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

    }
    
    public String getString(OWLAnnotationValue owlAnnotationValue) {
        return ((OWLLiteral)owlAnnotationValue).getLiteral();
    }
    
    public Boolean getBoolean(OWLAnnotationValue owlAnnotationValue) {
        return ((OWLLiteral)owlAnnotationValue).getLiteral().equals("true");
    }
    
    @SuppressWarnings("unchecked")
    public T getOntologyTerm(OWLClass node) throws Exception {

        T term = clazz.newInstance();
        
        EntitySearcher.getAnnotationObjects(node, ontology).forEach(annotation -> {

            String key = annotation.getProperty().getIRI().getShortForm();
            
            if(key.equals("id")) {
                term.setCurie(getString(annotation.getValue()));
            }
            
            if(annotation.getProperty().isLabel() && key.equals("label")) {
                term.setName(getString(annotation.getValue()));
            }
            
            if(key.equals("IAO_0000115")) {

                ontology.annotationAssertionAxioms(node.getIRI()).forEach(annot -> {
                    
                    if(annot.isAnnotated()) {
                        annot.annotations().forEach(an -> {
                            String inkey = an.getProperty().getIRI().getShortForm();
                            //System.out.println(inkey);
                            if(inkey.equals("hasDbXref")) {
                                //System.out.println("Adding: " + an.getValue().toString());
                                if(term.getDefinitionUrls() == null) term.setDefinitionUrls(new ArrayList<>());
                                term.getDefinitionUrls().add(getString(an.getValue()));
                            }
                        });
                    }
                });
                term.setDefinition(getString(annotation.getValue()));
            }
            
            if(key.equals("deprecated")) {
                term.setObsolete(getBoolean(annotation.getValue()));
            }
            if(key.equals("hasOBONamespace")) {
                term.setNamespace(getString(annotation.getValue()));
            }

        });

        return term;

    }


}
