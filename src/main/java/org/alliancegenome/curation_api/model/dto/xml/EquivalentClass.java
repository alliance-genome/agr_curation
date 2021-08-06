package org.alliancegenome.curation_api.model.dto.xml;

import javax.xml.bind.annotation.XmlElement;

public class EquivalentClass {

	@XmlElement(name = "Class", namespace = "http://www.w3.org/2002/07/owl#")
	private RDFClass rdfClass;
	
	@XmlElement(name = "Restriction", namespace = "http://www.w3.org/2002/07/owl#")
	private Restriction restriction;
}
