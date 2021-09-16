package org.alliancegenome.curation_api.services.helpers;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class GenericOntologyLoader<T extends OntologyTerm> implements OWLObjectVisitor {

    private OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
    //private OWLDataFactory df = OWLManager.getOWLDataFactory();
    private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private OWLReasoner reasoner;
    private OWLOntology ontology;

    private String defaultNamespace;
    private Class<T> clazz;

    private HashMap<String, T> allNodes = new HashMap<>();

    public GenericOntologyLoader(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    public Map<String, T> load(String fullText) throws Exception {
        File outfile = new File("tmp.file2.owl");
        log.info("Input data size: " + fullText.length());
        BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
        writer.append(fullText);
        writer.flush();
        writer.close();
        return load(outfile);
    }
    
    public Map<String, T> load(File infile) throws Exception {

        log.info("Loading Ontology File");
        IRI term_iri = IRI.create(infile);
        log.info("Loading Ontology File Finished");
        
        ontology = manager.loadOntologyFromOntologyDocument(term_iri);

        ontology.annotations().forEach(a -> {
            String key = a.getProperty().getIRI().getShortForm();
            log.info(key + ": " + getString(a.getValue()));
            if(key.equals("default-namespace")) {
                defaultNamespace = getString(a.getValue());
            }
        });

        OWLClass root = manager.getOWLDataFactory().getOWLThing();

        log.info("Ontology Loaded...");
        log.info("Document IRI: " + term_iri);
        log.info("Ontology : " + ontology.getOntologyID());
        log.info("Default Namespace : " + defaultNamespace);
        log.info("Format        : " + manager.getOntologyFormat(ontology));

        reasoner = reasonerFactory.createReasoner(ontology);

        log.info("Traversing Ontology");
        T rootTerm = traverse(root, 0);
        log.info("Finished Traversing Ontology");
        
        return allNodes;

    }

    public T traverse(OWLClass parent, int depth) throws Exception {

        T termParent = null;

        if (reasoner.isSatisfiable(parent)) {

            termParent = getOntologyTerm(parent);

            if((termParent.getNamespace() != null && termParent.getNamespace().equals(defaultNamespace)) || depth == 0) {
                //System.out.println(termParent);

                if(allNodes.containsKey(termParent.getCurie())) {
                        return allNodes.get(termParent.getCurie());
                } else {
                    if(termParent.getCurie() != null) {
                        allNodes.put(termParent.getCurie(), termParent);
                    }
                }

                for(OWLClass child: reasoner.getSubClasses(parent, true).entities().collect(Collectors.toList())) {

                    if (!child.equals(parent)) {
                        try {
                            T childTerm = traverse(child, depth + 1);
                            
//                          TODO LinkML to define the following fields                          
//                          if(childTerm != null) {
//                              
//                              termParent.addChild(childTerm);
//                              if(termParent.getCurie() != null) {
//                                  childTerm.addParent(termParent);
//                              }
//
//                              childTerm.addAncestor(termParent);
//                              if(termParent.getAncestors() != null) {
//                                  for(OntologyTerm a: termParent.getAncestors()) {
//                                      if(!childTerm.getAncestors().contains(a)) {
//                                          childTerm.addAncestor(a);
//                                      }
//                                  }
//                              }
//                              termParent.addDescendant(childTerm);
//                              if(childTerm.getDescendants() != null) {
//                                  for(OntologyTerm d: childTerm.getDescendants()) {
//                                      if(!termParent.getDescendants().contains(d)) {
//                                          termParent.addDescendant(d);
//                                      }
//                                  }
//                              }
//                              
//                          }
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

        }

        return termParent;

    }
    
    public String getIRIShortForm(OWLAnnotationValue owlAnnotationValue) {
        if(owlAnnotationValue.isIRI())
            return ((IRI)owlAnnotationValue).getShortForm();
        return "";
    }

    public String getString(OWLAnnotationValue owlAnnotationValue) {
        if(owlAnnotationValue.isLiteral())
            return ((OWLLiteral)owlAnnotationValue).getLiteral();
        if(owlAnnotationValue.isIRI())
            return ((IRI)owlAnnotationValue).getIRIString();
        return "";
    }

    public Boolean getBoolean(OWLAnnotationValue owlAnnotationValue) {
        return ((OWLLiteral)owlAnnotationValue).getLiteral().equals("true");
    }

    @SuppressWarnings("unchecked")
    public T getOntologyTerm(OWLClass node) throws Exception {

        T term = clazz.getDeclaredConstructor().newInstance();
        term.setObsolete(false);

        EntitySearcher.getAnnotationObjects(node, ontology).forEach(annotation -> {

            String key = annotation.getProperty().getIRI().getShortForm();

            if(key.equals("id")) {
                term.setCurie(getString(annotation.getValue()));
            }

            else if(annotation.getProperty().isLabel() && key.equals("label")) {
                term.setName(getString(annotation.getValue()));
            }

            else if(key.equals("IAO_0000115")) {

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

            else if(key.equals("deprecated")) {
                term.setObsolete(getBoolean(annotation.getValue()));
            }
            else if(key.equals("hasOBONamespace")) {
                term.setNamespace(getString(annotation.getValue()));
            }
            else if(key.equals("hasExactSynonym") || key.equals("hasRelatedSynonym")) {
                if(term.getSynonyms() == null) term.setSynonyms(new ArrayList<>());
                term.getSynonyms().add(getString(annotation.getValue()));
            }
            else if(key.equals("hasAlternativeId")) {
                if(term.getSecondaryIdentifiers() == null) term.setSecondaryIdentifiers(new ArrayList<>());
                term.getSecondaryIdentifiers().add(getString(annotation.getValue()));
            }
            else if(key.equals("hasDbXref")) {
                if(term.getCrossReferences() == null) term.setCrossReferences(new ArrayList<>());
                CrossReference ref = new CrossReference();
                ref.setCurie(getString(annotation.getValue()));
                term.getCrossReferences().add(ref);
            }
            else if(key.equals("inSubset")) {
                if(term.getSubsets() == null) term.setSubsets(new ArrayList<>());
                term.getSubsets().add(getIRIShortForm(annotation.getValue()));
            }
            else {
                //log.info(key + " -> " + getString(annotation.getValue()));
            }

        });

        return term;

    }

    
    

}
