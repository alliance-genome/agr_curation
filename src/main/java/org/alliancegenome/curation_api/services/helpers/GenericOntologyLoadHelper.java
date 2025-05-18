package org.alliancegenome.curation_api.services.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.jboss.logging.Logger.Level;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;

import io.quarkus.logging.Log;

public class GenericOntologyLoadHelper<T extends OntologyTerm> implements OWLObjectVisitor {

	private ElkReasonerFactory reasonerFactory = new ElkReasonerFactory();
	private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private OWLObjectProperty partOfProperty;

	private OWLReasoner reasoner;
	private OWLOntology ontology;

	private GenericOntologyLoadConfig config;
	private String defaultNamespace;
	private Class<T> clazz;

	private HashMap<String, T> allNodes = new HashMap<>();
	private HashSet<String> traversedNodes = new HashSet<String>();

	private ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);

	public GenericOntologyLoadHelper(Class<T> clazz) {
		this.clazz = clazz;
		this.config = new GenericOntologyLoadConfig();
	}

	public GenericOntologyLoadHelper(Class<T> clazz, GenericOntologyLoadConfig config) {
		this.clazz = clazz;
		this.config = config;
	}

	public Map<String, T> load(String fullText) throws Exception {
		File outfile = new File("tmp.file2.owl"); // TODO: fix so multiple loads do not overwrite each other Generate random name
		log("Input data size: " + fullText.length());
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		writer.append(fullText);
		writer.flush();
		writer.close();
		Map<String, T> ret = load(new FileInputStream(outfile));
		outfile.delete();
		return ret;
	}

	public Map<String, T> load(InputStream inStream) throws Exception {

		log("Loading Ontology File");
		ontology = manager.loadOntologyFromOntologyDocument(inStream);
		log("Loading Ontology File Finished");

		ontology.annotations().forEach(a -> {
			String key = a.getProperty().getIRI().getShortForm();
			log(key + ": " + getString(a.getValue()));
			if (key.equals("default-namespace")) {
				defaultNamespace = getString(a.getValue());
			}
		});

		HashSet<String> requiredNamespaces = config.getAltNameSpaces();
		if (requiredNamespaces.isEmpty()) {
			if (defaultNamespace != null) {
				requiredNamespaces.add(defaultNamespace);
			}
		}

		log("Ontology Loaded...");
		log("Ontology : " + ontology.getOntologyID());
		log("Default Namespace : " + defaultNamespace);
		log("Format		: " + manager.getOntologyFormat(ontology));

		reasoner = reasonerFactory.createReasoner(ontology);

		OWLObjectProperty rootProperty = manager.getOWLDataFactory().getOWLTopObjectProperty();

		if (config.getLoadObjectProperties()) {
			log("Traversing Object Properties");
			traverseProperties(rootProperty, 0);
			log("Finished Traversing Object Properties: " + allNodes.size());
			return allNodes;
		} else {
			log("Looking for Part_Of object property: ");
			partOfProperty = traverseSearchProperties(rootProperty, "part_of");
			log("Part of Found? : " + partOfProperty);
		}

		OWLClass root = manager.getOWLDataFactory().getOWLThing();

		log("Traversing Ontology");
		ph.startProcess("Traversing Ontology: " + clazz.getSimpleName());
		traverse(root, 0, requiredNamespaces);
		ph.finishProcess();
		log("Finished Traversing Ontology: " + allNodes.size());

		return allNodes;

	}

	public Boolean hasChebiXref(T term) {

		if (CollectionUtils.isNotEmpty(term.getSynonyms())) {
			for (Synonym synonym : term.getSynonyms()) {
				if (synonym.getName().startsWith("CHEBI:")) {
					return true;
				}
			}
		}

		if (CollectionUtils.isNotEmpty(term.getCrossReferences())) {
			for (CrossReference xref : term.getCrossReferences()) {
				if (xref.getReferencedCurie().startsWith("CHEBI:")) {
					return true;
				}
			}
		}

		return false;
	}

	public T traverse(OWLClass currentTreeNode, int depth, HashSet<String> requiredNamespaces) throws Exception {

		T currentTerm = null;

		if (reasoner.isSatisfiable(currentTreeNode)) {
			ph.progressProcess();
			currentTerm = getOntologyTerm(currentTreeNode);

			boolean isNodeInOntology = isNodeInOntology(currentTreeNode, currentTerm, requiredNamespaces);

			if (isNodeInOntology) {
				if (!allNodes.containsKey(currentTerm.getCurie())) {
					allNodes.put(currentTerm.getCurie(), currentTerm);
				} else {
					currentTerm = allNodes.get(currentTerm.getCurie());
				}
			}

			if (traversedNodes.contains(currentTerm.getCurie())) {
				return currentTerm;
			} else {
				traversedNodes.add(currentTerm.getCurie());
			}

			if (isNodeInOntology) {
				HashSet<OntologyTermClosure> ancestors = new HashSet<OntologyTermClosure>();
				HashSet<String> relationshipTypes = new HashSet<String>();
				traverseToRoot(currentTreeNode, "self", currentTerm, relationshipTypes, 0, requiredNamespaces, ancestors);
				currentTerm.setAncestors(new HashSet<>(ancestors));
			}

			for (OWLClass childTermNode : reasoner.getSubClasses(currentTreeNode, true).entities().collect(Collectors.toList())) {

				if (!childTermNode.equals(currentTreeNode)) {
					try {
						T childTerm = traverse(childTermNode, depth + 1, requiredNamespaces);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		return currentTerm;

	}

	private void traverseToRoot(OWLClass currentTreeNode, String relationType, OntologyTerm originalNode, HashSet<String> relationshipTypes, int depth, HashSet<String> requiredNamespaces, HashSet<OntologyTermClosure> ancestors) throws Exception {
		List<Map.Entry<OWLClass, String>> parents = new ArrayList<>();

		Set<OWLSubClassOfAxiom> parentsAxioms = ontology.getSubClassAxiomsForSubClass(currentTreeNode);
		for (OWLSubClassOfAxiom sub : parentsAxioms) {
			OWLClassExpression exp = sub.getSuperClass();
			if (!exp.isAnonymous()) {
				// is_a
				parents.add(new AbstractMap.SimpleEntry<>(exp.asOWLClass(), "is_a"));
			} else if (exp instanceof OWLObjectSomeValuesFrom) {
				OWLObjectSomeValuesFrom restriction = (OWLObjectSomeValuesFrom) exp;
				if (partOfProperty != null && restriction.getProperty().equals(partOfProperty) && !restriction.getFiller().isAnonymous()) {
					parents.add(new AbstractMap.SimpleEntry<>(restriction.getFiller().asOWLClass(), "part_of"));
				}
			}
		}

		T currentTerm = null;

		if (reasoner.isSatisfiable(currentTreeNode)) {

			currentTerm = getOntologyTerm(currentTreeNode);

			if (isNodeInOntology(currentTreeNode, currentTerm, requiredNamespaces)) {

				T existingNode = allNodes.get(currentTerm.getCurie());

				if (existingNode == null) {
					allNodes.put(currentTerm.getCurie(), currentTerm);
					existingNode = currentTerm;
				}

				if (relationType != null && !relationType.equals("self")) {
					OntologyTermClosure closure = new OntologyTermClosure();
					closure.setClosureSubject(originalNode);
					closure.setClosureObject(existingNode);
					closure.getClosureTypes().addAll(relationshipTypes);
					closure.setDistance(depth);
					ancestors.add(closure);

					OntologyTermClosure closure2 = new OntologyTermClosure();
					closure2.setClosureSubject(originalNode);
					closure2.setClosureObject(existingNode);
					closure2.getClosureTypes().add("is_a");
					closure2.getClosureTypes().add("part_of");
					closure2.setDistance(depth);
					ancestors.add(closure2);

				} else {
					// We are the current node and need to traverse first
				}
			}

			for (Entry<OWLClass, String> parent : parents) {
				boolean alreadyContains = relationshipTypes.contains(parent.getValue());
				if (!alreadyContains) {
					relationshipTypes.add(parent.getValue());
				}
				traverseToRoot(parent.getKey(), parent.getValue(), originalNode, relationshipTypes, depth + 1, requiredNamespaces, ancestors);
				if (!alreadyContains) {
					relationshipTypes.remove(parent.getValue());
				}
			}
		}
	}

	private boolean isNodeInOntology(OWLClass currentTreeNode, T currentTerm, HashSet<String> requiredNamespaces) {
		boolean condition0 = currentTerm.getCurie() != null;
		boolean condition1 = currentTerm.getNamespace() != null;
		boolean condition2 = requiredNamespaces.contains(currentTerm.getNamespace());
		boolean condition3 = config.getLoadOnlyIRIPrefix() != null;
		boolean condition4 = currentTreeNode.getIRI().getShortForm().startsWith(config.getLoadOnlyIRIPrefix() + "_");

		boolean condition5 = !config.getIgnoreEntitiesWithChebiXref();
		boolean condition6 = !hasChebiXref(currentTerm);

		// CHECKSTYLE:OFF: UnnecessaryParentheses
		return condition0 && ((condition1 && condition2 && !condition3) || (condition3 && condition4)) && (condition5 || condition6);
		// CHECKSTYLE:ON: UnnecessaryParentheses
	}

	public void printDepthMessage(int depth, String message) {
		String tabs = "";
		for (int i = 0; i < depth; i++) {
			tabs += "\t";
		}

		log(tabs + message);
	}

	public String getIRIShortForm(OWLAnnotationValue owlAnnotationValue) {
		if (owlAnnotationValue.isIRI()) {
			return ((IRI) owlAnnotationValue).getShortForm();
		}
		return "";
	}

	public String getString(OWLAnnotationValue owlAnnotationValue) {
		if (owlAnnotationValue.isLiteral()) {
			return ((OWLLiteral) owlAnnotationValue).getLiteral();
		}
		if (owlAnnotationValue.isIRI()) {
			return ((IRI) owlAnnotationValue).getIRIString();
		}
		return "";
	}

	public Boolean getBoolean(OWLAnnotationValue owlAnnotationValue) {
		return ((OWLLiteral) owlAnnotationValue).getLiteral().equals("true");
	}

	public T getOntologyTerm(OWLClass node) throws Exception {

		T term = clazz.getDeclaredConstructor().newInstance();
		term.setObsolete(false);

		EntitySearcher.getAnnotationObjects(node, ontology).forEach(annotation -> {
			String key = annotation.getProperty().getIRI().getShortForm();
			parseAnnotation(annotation, node, term, key);
		});

		if (term.getCurie() == null && EntitySearcher.getAnnotationObjects(node, ontology).count() > 0) {
			term.setCurie(node.getIRI().getFragment().replaceFirst("_", ":"));
		}

		return term;

	}

	private T parseAnnotation(OWLAnnotation annotation, OWLClass node, T term, String key) {
		if (key.equals("id")) {
			term.setCurie(getString(annotation.getValue()));
		} else if (annotation.getProperty().isLabel() && key.equals("label")) {
			term.setName(getString(annotation.getValue()));
		} else if (key.equals("IAO_0000115")) {

			if (node != null) {
				ontology.annotationAssertionAxioms(node.getIRI()).forEach(annot -> {
					if (annot.isAnnotated()) {
						annot.annotations().forEach(an -> {
							String inkey = an.getProperty().getIRI().getShortForm();
							// log(inkey);
							if (inkey.equals("hasDbXref")) {
								// log("Adding: " + an.getValue().toString());
								if (term.getDefinitionUrls() == null) {
									term.setDefinitionUrls(new ArrayList<>());
								}
								term.getDefinitionUrls().add(getString(an.getValue()));
							}
						});
					}
				});
			}
			term.setDefinition(getString(annotation.getValue()));
		} else if (key.equals("deprecated")) {
			term.setObsolete(getBoolean(annotation.getValue()));
		} else if (key.equals("hasOBONamespace")) {
			term.setNamespace(getString(annotation.getValue()));
		} else if (key.equals("hasExactSynonym")) {
			if (term.getSynonyms() == null) {
				term.setSynonyms(new ArrayList<>());
			}
			Synonym synonym = new Synonym();
			String mainSynonymValue = getString(annotation.getValue());

			if (node != null) {
				ontology.annotationAssertionAxioms(node.getIRI()).forEach(axiom -> {
					if (axiom.isAnnotated()) {
						String valueText = getString(axiom.getValue());
						if (valueText.equals(mainSynonymValue)) {
							axiom.annotations().forEach(an -> {
								String inkey = an.getProperty().getIRI().getShortForm();
								if (inkey.equals("hasSynonymType")) {
									String shortForm = getIRIShortForm(an.getValue());
									if (shortForm.equals("DISPLAY_SYNONYM")) {
										synonym.setIsDisplaySynonym(true);
									}
								}
							});
						}
					}
				});
			}
			synonym.setName(mainSynonymValue);
			synonym.setHasExactSynonym(true);
			term.getSynonyms().add(synonym);
		} else if (key.equals("hasRelatedSynonym")) {
			if (term.getSynonyms() == null) {
				term.setSynonyms(new ArrayList<>());
			}
			Synonym synonym = new Synonym();
			synonym.setName(getString(annotation.getValue()));
			synonym.setHasRelatedSynonym(true);
			term.getSynonyms().add(synonym);
		} else if (key.equals("hasNarrowSynonym")) {
			if (term.getSynonyms() == null) {
				term.setSynonyms(new ArrayList<>());
			}
			Synonym synonym = new Synonym();
			synonym.setName(getString(annotation.getValue()));
			synonym.setHasNarrowSynonym(true);
			term.getSynonyms().add(synonym);
		} else if (key.equals("hasBroadSynonym")) {
			if (term.getSynonyms() == null) {
				term.setSynonyms(new ArrayList<>());
			}
			Synonym synonym = new Synonym();
			synonym.setName(getString(annotation.getValue()));
			synonym.setHasBroadSynonym(true);
			term.getSynonyms().add(synonym);
		} else if (key.equals("hasAlternativeId")) {
			if (term.getSecondaryIdentifiers() == null) {
				term.setSecondaryIdentifiers(new ArrayList<>());
			}
			term.getSecondaryIdentifiers().add(getString(annotation.getValue()));
		} else if (key.equals("hasDbXref") || key.equals("database_cross_reference")) {
			if (term.getCrossReferences() == null) {
				term.setCrossReferences(new ArrayList<>());
			}
			CrossReference ref = new CrossReference();
			ref.setReferencedCurie(getString(annotation.getValue()));
			ref.setDisplayName(getString(annotation.getValue()));
			term.getCrossReferences().add(ref);
		} else if (key.equals("inSubset")) {
			if (term.getSubsets() == null) {
				term.setSubsets(new ArrayList<>());
			}
			term.getSubsets().add(getIRIShortForm(annotation.getValue()));
		} else {
			// log.info(key + " -> " + getString(annotation.getValue()));
		}

		return term;
	}

	public OWLObjectProperty traverseSearchProperties(OWLObjectProperty rootTreeProperty, String searchString) {

		for (OWLAnnotation annotation : EntitySearcher.getAnnotationObjects(rootTreeProperty.getNamedProperty(), ontology).toList()) {
			String key = annotation.getProperty().getIRI().getShortForm();
			if (key.equals("id")) {
				String id = getString(annotation.getValue());
				if (id.equals(searchString)) {
					return rootTreeProperty;
				}
			}
		}

		for (OWLObjectPropertyExpression childTermPropertyExpression : reasoner.getSubObjectProperties(rootTreeProperty, true).entities().collect(Collectors.toList())) {
			OWLObjectProperty childProperty = traverseSearchProperties(childTermPropertyExpression.getNamedProperty(), searchString);
			if (childProperty != null) {
				return childProperty;
			}
		}

		return null;
	}

	public T traverseProperties(OWLObjectProperty currentTreeProperty, int depth) throws Exception {

		T currentTerm = null;

		ph.progressProcess();
		currentTerm = getOntologyTermFromProperty(currentTreeProperty);

		boolean isPropertyInOntology = isPropertyInOntology(currentTreeProperty);

		if (isPropertyInOntology && currentTerm.getCurie() != null) {
			if (!allNodes.containsKey(currentTerm.getCurie())) {
				allNodes.put(currentTerm.getCurie(), currentTerm);
			} else {
				currentTerm = allNodes.get(currentTerm.getCurie());
			}
		}

		if (traversedNodes.contains(currentTerm.getCurie())) {
			return currentTerm;
		}

		traversedNodes.add(currentTerm.getCurie());

		// TODO: Ontology: turn back on -- Required for RO
		if (isPropertyInOntology) {
//			HashSet<OntologyTerm> ancestors = new HashSet<OntologyTerm>();
//			traverseToRootProperty(currentTreeProperty, depth, ancestors);
//			ancestors.remove(currentTerm);
//			currentTerm.setIsaAncestors(new HashSet<>(ancestors));
		}

		for (OWLObjectPropertyExpression childTermPropertyExpression : reasoner.getSubObjectProperties(currentTreeProperty, true).entities().collect(Collectors.toList())) {
			if (!childTermPropertyExpression.getNamedProperty().toString().equals(currentTreeProperty.toString())) {
				try {
					T childTerm = traverseProperties(childTermPropertyExpression.getNamedProperty(), depth + 1);

					
					if (childTerm != null && currentTerm.getCurie() != null && isPropertyInOntology) {
						// TODO: Ontology: turn back on -- Required for RO
						//childTerm.addIsaParent(currentTerm);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return currentTerm;

	}

	public T getOntologyTermFromProperty(OWLObjectProperty property) throws Exception {

		T term = clazz.getDeclaredConstructor().newInstance();
		term.setObsolete(false);
		term.setCurie(property.getIRI().getFragment().replaceFirst("_", ":"));

		EntitySearcher.getAnnotationObjects(property, ontology).forEach(annotation -> {
			String key = annotation.getProperty().getIRI().getShortForm();
			parseAnnotation(annotation, null, term, key);
		});

		return term;

	}

	private void traverseToRootProperty(OWLObjectProperty currentTreeProperty, int depth, HashSet<OntologyTerm> ancestors) throws Exception {
		List<OWLObjectPropertyExpression> parents = reasoner.getSuperObjectProperties(currentTreeProperty, true).entities().collect(Collectors.toList());

		T currentTerm = null;

		currentTerm = getOntologyTermFromProperty(currentTreeProperty);

		if (currentTerm.getCurie() != null && isPropertyInOntology(currentTreeProperty)) {

			T existingNode = allNodes.get(currentTerm.getCurie());

			if (existingNode == null) {
				allNodes.put(currentTerm.getCurie(), currentTerm);
				existingNode = currentTerm;
			}

			if (!ancestors.contains(existingNode)) {
				ancestors.add(existingNode);
			}
		}

		for (OWLObjectPropertyExpression parent : parents) {
			traverseToRootProperty(parent.getNamedProperty(), depth + 1, ancestors);
		}
	}

	private boolean isPropertyInOntology(OWLObjectProperty property) {
		if (config.getLoadOnlyIRIPrefix() == null) {
			return true;
		}

		return property.getIRI().getFragment().startsWith(config.getLoadOnlyIRIPrefix() + "_");
	}

	private void log(String message) {
		log(Level.INFO, message);
	}

	private void log(Level level, String message) {
		Log.log(level, message);
		// System.out.println(message);
	}

	public void printTree(String string, Map<String, T> map) {

	}

}
