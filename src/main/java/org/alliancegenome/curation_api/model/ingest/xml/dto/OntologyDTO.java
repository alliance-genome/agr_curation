package org.alliancegenome.curation_api.model.ingest.xml.dto;

import javax.xml.bind.annotation.*;

import lombok.Data;

@Data
@XmlRootElement(name = "Ontology", namespace = "http://www.w3.org/2002/07/owl#")
@XmlAccessorType (XmlAccessType.FIELD)
public class OntologyDTO {

	@XmlAttribute(name = "about")
	private String about;
	
	@XmlElement(name = "versionIRI", namespace = "http://www.w3.org/2002/07/owl#")
	private String versionIRI;
	
	@XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
	private String imports;
	
	@XmlElement(namespace = "http://purl.obolibrary.org/obo/")
	private String IAO_0000700;
	
	@XmlElement(namespace = "http://purl.org/dc/elements/1.1/")
	private String description;
	
	@XmlElement(namespace = "http://purl.org/dc/elements/1.1/")
	private String title;
	
	@XmlElement(namespace = "http://purl.org/dc/terms/")
	private String license;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String date;
	
	@XmlElement(name = "default-namespace", namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String defaultNamespace;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String hasOBOFormatVersion;
	
	@XmlElement(name = "saved-by", namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String savedBy;
	
	@XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
	private String comment;
	
}
