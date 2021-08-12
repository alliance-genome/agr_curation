package org.alliancegenome.curation_api.model.dto.xml;

import javax.xml.bind.annotation.*;

import lombok.*;

@Data
@XmlRootElement(name = "RDF", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
@XmlAccessorType (XmlAccessType.FIELD)
public class RDF {

	@XmlAttribute(name = "base")
	private String base;
	
	@XmlElement(name = "Description", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
	private Description description;
	
	@XmlElement(name = "Ontology", namespace = "http://www.w3.org/2002/07/owl#")
	private OntologyDTO ontology;
	
	@XmlElement(name = "AnnotationProperty", namespace = "http://www.w3.org/2002/07/owl#")
	private AnnotationProperty[] annotationProperties;
	
	@XmlElement(name = "Axiom", namespace = "http://www.w3.org/2002/07/owl#")
	private Axiom[] axioms;
	
	@XmlElement(name = "ObjectProperty", namespace = "http://www.w3.org/2002/07/owl#")
	private ObjectProperty[] objectProperties;
	
	@XmlElement(name = "Class", namespace = "http://www.w3.org/2002/07/owl#")
	private RDFClass[] classes;

}
