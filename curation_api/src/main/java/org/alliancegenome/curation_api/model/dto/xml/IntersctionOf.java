package org.alliancegenome.curation_api.model.dto.xml;

import javax.xml.bind.annotation.XmlElement;

public class IntersctionOf {

	@XmlElement(name = "Description", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
	private Description description;
	
	@XmlElement(name = "Restriction", namespace = "http://www.w3.org/2002/07/owl#")
	private Restriction restriction;
	
	@XmlElement(name = "Class", namespace = "http://www.w3.org/2002/07/owl#")
	private RDFClass rdfClass;
}
