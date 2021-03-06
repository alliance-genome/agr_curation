package org.alliancegenome.curation_api.services.helpers;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.apache.commons.collections.CollectionUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class GenericOntologyLoadHelper<T extends OntologyTerm> implements OWLObjectVisitor {

	private OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
	//private OWLDataFactory df = OWLManager.getOWLDataFactory();
	private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private OWLReasoner reasoner;
	private OWLOntology ontology;

	private GenericOntologyLoadConfig config;
	private String defaultNamespace = null;
	private Class<T> clazz;

	private HashMap<String, T> allNodes = new HashMap<>();

	public GenericOntologyLoadHelper(Class<T> clazz) {
		this.clazz = clazz;
		this.config = new GenericOntologyLoadConfig();
	}
	
	public GenericOntologyLoadHelper(Class<T> clazz, GenericOntologyLoadConfig config) {
		this.clazz = clazz;
		this.config = config;
	}
	
	public Map<String, T> load(String fullText) throws Exception {
		File outfile = new File("tmp.file2.owl"); // TODO fix so multiple loads do not overwrite each other Generate random name
		log.info("Input data size: " + fullText.length());
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		writer.append(fullText);
		writer.flush();
		writer.close();
		Map<String, T> ret = load(new FileInputStream(outfile));
		outfile.delete();
		return ret;
	}
	
	public Map<String, T> load(InputStream inStream) throws Exception {

		log.info("Loading Ontology File");
		ontology = manager.loadOntologyFromOntologyDocument(inStream);
		log.info("Loading Ontology File Finished");
		
		ontology.annotations().forEach(a -> {
			String key = a.getProperty().getIRI().getShortForm();
			log.info(key + ": " + getString(a.getValue()));
			if(key.equals("default-namespace")) {
				defaultNamespace = getString(a.getValue());
			}
		});

		ArrayList<String> requiredNamespaces = config.getAltNameSpaces();
		if (requiredNamespaces.isEmpty()) {
			if (defaultNamespace != null) {
				requiredNamespaces.add(defaultNamespace);
			}
		}
		
		OWLClass root = manager.getOWLDataFactory().getOWLThing();

		log.info("Ontology Loaded...");
		log.info("Ontology : " + ontology.getOntologyID());
		log.info("Default Namespace : " + defaultNamespace);
		log.info("Format		: " + manager.getOntologyFormat(ontology));

		reasoner = reasonerFactory.createReasoner(ontology);

		log.info("Traversing Ontology");
		traverse(root, 0, requiredNamespaces);
		log.info("Finished Traversing Ontology");
		
		return allNodes;

	}
	
	public Boolean hasChebiXref(T term) {
		
		if (CollectionUtils.isNotEmpty(term.getSynonyms())) {
			for (String synonym : term.getSynonyms()) {
				if (synonym.startsWith("CHEBI:")) {
					return true;
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(term.getCrossReferences())) {
			for (CrossReference xref : term.getCrossReferences()) {
				if (xref.getCurie().startsWith("CHEBI:")) {
					return true;
				}
			}
		}
		
		return false;
	}

	public T traverse(OWLClass parent, int depth, ArrayList<String> requiredNamespaces) throws Exception {

		T termParent = null;

		if (reasoner.isSatisfiable(parent)) {

			termParent = getOntologyTerm(parent);
	
			if(
				(
					(termParent.getNamespace() != null && requiredNamespaces.contains(termParent.getNamespace())) ||	
					(config.getLoadOnlyIRIPrefix() != null && parent.getIRI().getShortForm().startsWith(config.getLoadOnlyIRIPrefix() + "_"))
				) && (
					!config.getIgnoreEntitiesWithChebiXref() || !hasChebiXref(termParent)
				)
			) {
				//System.out.println(termParent);

				if(allNodes.containsKey(termParent.getCurie())) {
						return allNodes.get(termParent.getCurie());
				} else {
					if(termParent.getCurie() != null) {
						allNodes.put(termParent.getCurie(), termParent);
					}
				}
			}

			for(OWLClass child: reasoner.getSubClasses(parent, true).entities().collect(Collectors.toList())) {

				if (!child.equals(parent)) {
					try {
						T childTerm = traverse(child, depth + 1, requiredNamespaces);
							
//							TODO LinkML to define the following ontology relationship fields
//							if(childTerm != null) {
//								
//								termParent.addChild(childTerm);
//								if(termParent.getCurie() != null) {
//									childTerm.addParent(termParent);
//								}
//
//								childTerm.addAncestor(termParent);
//								if(termParent.getAncestors() != null) {
//									for(OntologyTerm a: termParent.getAncestors()) {
//										if(!childTerm.getAncestors().contains(a)) {
//											childTerm.addAncestor(a);
//										}
//									}
//								}
//								termParent.addDescendant(childTerm);
//								if(childTerm.getDescendants() != null) {
//									for(OntologyTerm d: childTerm.getDescendants()) {
//										if(!termParent.getDescendants().contains(d)) {
//											termParent.addDescendant(d);
//										}
//									}
//								}
//								
//							}
						
					} catch (Exception e) {
						e.printStackTrace();
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
			else if(key.equals("hasDbXref") || key.equals("database_cross_reference")) {
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
		
		if (term.getCurie() == null && EntitySearcher.getAnnotationObjects(node, ontology).count() > 0) {
			term.setCurie(node.getIRI().getFragment().replaceFirst("_", ":"));	
		}

		return term;

	}

	
	

}
