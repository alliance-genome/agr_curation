package org.alliancegenome.curation_api.model.dto.xml;

import javax.xml.bind.annotation.*;

import lombok.*;

@Data
@ToString
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@XmlRootElement(name = "ObjectProperty", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
@XmlAccessorType (XmlAccessType.FIELD)
public class ObjectProperty {
	
	private String about;
	
	@XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
	private String label;
	
	@XmlElement(namespace = "http://purl.obolibrary.org/obo/")
	private String IAO_0000115;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String id;
	
	@XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
	private String subPropertyOf;
	
	@XmlElement(namespace = "http://www.geneontology.org/formats/oboInOwl#")
	private String hasOBONamespace;
	
	
	private String deprecated;

}
