package org.alliancegenome.curation_api.model.dto.xml;

import javax.xml.bind.annotation.*;

import lombok.*;

@Data
@XmlRootElement(name = "AnnotationProperty", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
@XmlAccessorType (XmlAccessType.FIELD)
public class AnnotationProperty {
	
	private String lang;
	private String about;
	
	@XmlElement(name = "IAO_0000115", namespace = "http://purl.obolibrary.org/obo/")
	private String IAO_0000115;
	
	@XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
	private String label;
	
	@XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
	private String isDefinedBy;
	
	@XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
	private String comment;
	
	@XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
	private String subPropertyOf;

	

	
}
