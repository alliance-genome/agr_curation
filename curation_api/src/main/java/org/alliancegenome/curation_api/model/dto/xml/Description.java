package org.alliancegenome.curation_api.model.dto.xml;

import javax.xml.bind.annotation.*;

import lombok.*;

@Data
@ToString
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@XmlRootElement(name = "Description", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
@XmlAccessorType (XmlAccessType.FIELD)
public class Description {
	
	@XmlElement(name = "IAO_0000115", namespace = "http://purl.obolibrary.org/obo/")
	private String IAO_0000115;
	
	@XmlElement(namespace = "http://www.w3.org/2000/01/rdf-schema#")
	private String label;

	@XmlElement(namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
	private String type;
	
	@XmlElement(namespace = "http://www.w3.org/2002/07/owl#")
	private Members[] members;
}
